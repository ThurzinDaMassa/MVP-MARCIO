const text=document.querySelector('#essayText');
const title=document.querySelector('#essayTitle');
const theme=document.querySelector('#essayTheme');
const draftKey='cola-redacao-rascunho';
if(text){
  const updateMetrics=()=>{
    const value=text.value.trim();
    const words=value?value.split(/\s+/).length:0;
    const paragraphs=value?value.split(/\n\s*\n/).filter(Boolean).length:0;
    document.querySelector('#wordCount').textContent=words;
    document.querySelector('#charCount').textContent=text.value.length;
    document.querySelector('#paragraphCount').textContent=paragraphs;
    document.querySelector('#readingTime').textContent=`${Math.max(1,Math.ceil(words/200))} min`;
    document.querySelector('#goalProgress').style.width=`${Math.min(100,(words/250)*100)}%`;
    document.querySelector('#goalLabel').textContent=words===0?'Comece a escrever':words<250?`Faltam ${250-words} palavras`:words<=350?'Meta atingida':'Texto acima da faixa sugerida';
  };
  const saveDraft=()=>{localStorage.setItem(draftKey,JSON.stringify({titulo:title?.value||'',tema:theme?.value||'',texto:text.value,salvoEm:new Date().toISOString()}));const status=document.querySelector('#draftStatus');if(status)status.textContent='Rascunho salvo agora';};
  if(!text.value&&!title?.value&&!theme?.value){try{const draft=JSON.parse(localStorage.getItem(draftKey));if(draft?.texto){title.value=draft.titulo||'';theme.value=draft.tema||'';text.value=draft.texto||'';document.querySelector('#draftStatus').textContent='Rascunho recuperado';}}catch(e){localStorage.removeItem(draftKey);}}
  let timer;[text,title,theme].filter(Boolean).forEach(field=>field.addEventListener('input',()=>{updateMetrics();clearTimeout(timer);timer=setTimeout(saveDraft,500);}));
  document.querySelector('#clearDraft')?.addEventListener('click',()=>{if(confirm('Limpar o rascunho atual?')){[text,title,theme].filter(Boolean).forEach(f=>f.value='');localStorage.removeItem(draftKey);updateMetrics();document.querySelector('#draftStatus').textContent='Rascunho limpo';}});
  const fileInput=document.querySelector('#textFile');
  document.querySelector('#importText')?.addEventListener('click',()=>fileInput?.click());
  fileInput?.addEventListener('change',async()=>{const file=fileInput.files?.[0];if(!file)return;if(file.size>1000000){alert('Escolha um arquivo de texto com até 1 MB.');return;}text.value=await file.text();updateMetrics();saveDraft();fileInput.value='';});
  document.querySelector('#focusMode')?.addEventListener('click',event=>{document.body.classList.toggle('focus-writing');event.currentTarget.textContent=document.body.classList.contains('focus-writing')?'Sair do foco':'Modo foco';text.focus();});
  updateMetrics();
}
const form=document.querySelector('#essayForm');
if(form)form.addEventListener('submit',()=>{const b=document.querySelector('#submitEssay');b.disabled=true;b.textContent='✦ Analisando...';});
if(document.querySelector('.pro-report'))localStorage.removeItem(draftKey);

const historySearch=document.querySelector('#historySearch');
const scoreFilter=document.querySelector('#scoreFilter');
if(historySearch&&scoreFilter){const filterHistory=()=>{let visible=0;const query=historySearch.value.trim().toLowerCase();const score=Number(scoreFilter.value);document.querySelectorAll('.essay-row[data-search]').forEach(row=>{const show=row.dataset.search.includes(query)&&Number(row.dataset.score)>=score;row.hidden=!show;if(show)visible++;});document.querySelector('#historyEmpty').hidden=visible>0;};historySearch.addEventListener('input',filterHistory);scoreFilter.addEventListener('change',filterHistory);}

document.documentElement.classList.add('motion-ready');
const animated=document.querySelectorAll('.cards article,.stats article,.history,.essay-form,.result-grid>article,.cta');
animated.forEach((element,index)=>{
  element.classList.add('reveal');
  element.style.setProperty('--reveal-delay',`${Math.min(index*70,280)}ms`);
});
const observer=new IntersectionObserver(entries=>entries.forEach(entry=>{
  if(entry.isIntersecting){entry.target.classList.add('is-visible');observer.unobserve(entry.target);}
}),{threshold:.12});
animated.forEach(element=>observer.observe(element));

const askGemini=document.querySelector('#askGemini');
if(askGemini){
  const result=document.querySelector('#aiCoachResult');
  const escapeHtml=value=>String(value).replace(/[&<>'"]/g,char=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));
  askGemini.addEventListener('click',async()=>{
    const texto=text?.value.trim()||'',tema=theme?.value.trim()||'';
    result.hidden=false;result.classList.remove('error');
    if(!tema||texto.length<100){result.classList.add('error');result.textContent='Informe o tema e escreva ao menos 100 caracteres para receber dicas específicas.';return;}
    askGemini.disabled=true;askGemini.textContent='Analisando rascunho...';result.textContent='O Gemini está identificando os pontos de maior impacto.';
    try{
      const token=document.querySelector('meta[name="_csrf"]')?.content,header=document.querySelector('meta[name="_csrf_header"]')?.content;
      const headers={'Content-Type':'application/x-www-form-urlencoded'};if(token&&header)headers[header]=token;
      const response=await fetch('/redacoes/dicas',{method:'POST',headers,body:new URLSearchParams({tema,texto})});
      const data=await response.json();if(!response.ok)throw new Error(data.erro||'Não foi possível gerar as dicas.');
      const lista=itens=>itens.map(item=>`<li>${escapeHtml(item)}</li>`).join('');
      result.innerHTML=`<h3>Diagnóstico</h3><p>${escapeHtml(data.diagnostico)}</p><h3>Prioridades</h3><ul>${lista(data.prioridades||[])}</ul><h3>Como otimizar</h3><ul>${lista(data.sugestoes||[])}</ul>`;
    }catch(error){result.classList.add('error');result.textContent=error.message;}
    finally{askGemini.disabled=false;askGemini.textContent='Atualizar dicas';}
  });
}

const timerDisplay=document.querySelector('#timerDisplay');
if(timerDisplay){
  let remaining=90*60,interval=null;
  const drawTimer=()=>{const minutes=Math.floor(remaining/60),seconds=remaining%60;timerDisplay.textContent=`${String(minutes).padStart(2,'0')}:${String(seconds).padStart(2,'0')}`;timerDisplay.classList.toggle('ending',remaining<=600);};
  const toggle=document.querySelector('#timerToggle');
  toggle.addEventListener('click',()=>{if(interval){clearInterval(interval);interval=null;toggle.textContent='Continuar';return;}toggle.textContent='Pausar';interval=setInterval(()=>{if(remaining>0){remaining--;drawTimer();}else{clearInterval(interval);interval=null;toggle.textContent='Encerrado';timerDisplay.classList.add('finished');}},1000);});
  document.querySelector('#timerReset')?.addEventListener('click',()=>{clearInterval(interval);interval=null;remaining=90*60;toggle.textContent='Iniciar';timerDisplay.classList.remove('finished');drawTimer();});
  drawTimer();
}

const checklist=[...document.querySelectorAll('.enem-checklist input[type="checkbox"]')];
if(checklist.length){
  const key='cola-redacao-checklist';
  try{const saved=JSON.parse(localStorage.getItem(key)||'[]');checklist.forEach((item,index)=>item.checked=Boolean(saved[index]));}catch(e){localStorage.removeItem(key);}
  checklist.forEach(item=>item.addEventListener('change',()=>localStorage.setItem(key,JSON.stringify(checklist.map(field=>field.checked)))));
}
