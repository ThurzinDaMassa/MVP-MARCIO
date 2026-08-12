const checks=[...document.querySelectorAll('[data-study]')];
function updateProgress(){
  const done=checks.filter(c=>c.checked).length;
  const percent=checks.length?Math.round(done/checks.length*100):0;
  document.querySelector('#progressValue')?.replaceChildren(String(percent));
  const bar=document.querySelector('#progressBar');if(bar)bar.style.width=`${percent}%`;
}
checks.forEach(check=>{check.checked=localStorage.getItem(`textozen-${check.dataset.study}`)==='1';check.addEventListener('change',()=>{localStorage.setItem(`textozen-${check.dataset.study}`,check.checked?'1':'0');updateProgress();});});
updateProgress();

const filterButtons=[...document.querySelectorAll('[data-filter]')];
filterButtons.forEach(button=>button.addEventListener('click',()=>{filterButtons.forEach(item=>item.classList.toggle('active',item===button));document.querySelectorAll('[data-competency]').forEach(card=>card.hidden=button.dataset.filter!=='all'&&card.dataset.competency!==button.dataset.filter);}));

const flashcards=[
  ['O que diferencia repertório citado de repertório produtivo?','O repertório produtivo é explicado e conectado diretamente à tese ou ao argumento, ajudando a sustentar o ponto de vista.'],
  ['Quais são os elementos centrais da proposta de intervenção?','Agente, ação, meio ou modo, finalidade e detalhamento, sempre relacionados ao problema discutido e respeitando os direitos humanos.'],
  ['Qual é a função da tese na introdução?','Delimitar o ponto de vista defendido e indicar a direção argumentativa que será desenvolvida no texto.'],
  ['Usar muitos conectivos garante 200 na Competência 4?','Não. É necessário que os mecanismos coesivos sejam variados, adequados e estabeleçam relações lógicas reais, sem uso mecânico.'],
  ['O que torna um argumento bem desenvolvido?','Explicar como e por que ele sustenta a tese, articulando causa, consequência, evidência, exemplo ou repertório pertinente.']
];
const flashQuestion=document.querySelector('#flashQuestion'),flashAnswer=document.querySelector('#flashAnswer');
if(flashQuestion&&flashAnswer){let current=0;const renderFlash=()=>{flashQuestion.textContent=flashcards[current][0];flashAnswer.textContent=flashcards[current][1];flashAnswer.hidden=true;document.querySelector('#revealFlash').textContent='Revelar resposta';document.querySelector('#flashCurrent').textContent=current+1;document.querySelector('#flashTotal').textContent=flashcards.length;};document.querySelector('#revealFlash')?.addEventListener('click',event=>{flashAnswer.hidden=!flashAnswer.hidden;event.currentTarget.textContent=flashAnswer.hidden?'Revelar resposta':'Ocultar resposta';});document.querySelector('#flashPrev')?.addEventListener('click',()=>{current=(current-1+flashcards.length)%flashcards.length;renderFlash();});document.querySelector('#flashNext')?.addEventListener('click',()=>{current=(current+1)%flashcards.length;renderFlash();});renderFlash();}

const fields=[...document.querySelectorAll('[data-plan]')];
const preview={tema:'pvTema',arg1:'pvArg1',arg2:'pvArg2',intervencao:'pvIntervencao'};
function renderPlan(){fields.forEach(f=>{const target=document.querySelector(`#${preview[f.dataset.plan]}`);if(target&&f.value.trim())target.textContent=f.value.trim();});}
fields.forEach(field=>{field.value=localStorage.getItem(`textozen-plan-${field.dataset.plan}`)||'';field.addEventListener('input',()=>{localStorage.setItem(`textozen-plan-${field.dataset.plan}`,field.value);renderPlan();});});
renderPlan();
document.querySelector('#clearPlan')?.addEventListener('click',()=>{if(confirm('Limpar todo o planejamento?')){fields.forEach(f=>{f.value='';localStorage.removeItem(`textozen-plan-${f.dataset.plan}`);});location.reload();}});
document.querySelector('#copyPlan')?.addEventListener('click',async()=>{const values=Object.fromEntries(fields.map(f=>[f.dataset.plan,f.value||'—']));const text=`TEMA: ${values.tema}\nTESE: ${values.tese}\nARGUMENTO 1: ${values.arg1}\nARGUMENTO 2: ${values.arg2}\nREPERTÓRIO: ${values.repertorio}\nINTERVENÇÃO: ${values.intervencao}`;await navigator.clipboard.writeText(text);document.querySelector('#saveNote').textContent='✓ Estrutura copiada';});
