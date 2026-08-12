const checks=[...document.querySelectorAll('[data-study]')];
function updateProgress(){
  const done=checks.filter(c=>c.checked).length;
  const percent=checks.length?Math.round(done/checks.length*100):0;
  document.querySelector('#progressValue')?.replaceChildren(String(percent));
  const bar=document.querySelector('#progressBar');if(bar)bar.style.width=`${percent}%`;
}
checks.forEach(check=>{check.checked=localStorage.getItem(`textozen-${check.dataset.study}`)==='1';check.addEventListener('change',()=>{localStorage.setItem(`textozen-${check.dataset.study}`,check.checked?'1':'0');updateProgress();});});
updateProgress();

const fields=[...document.querySelectorAll('[data-plan]')];
const preview={tema:'pvTema',arg1:'pvArg1',arg2:'pvArg2',intervencao:'pvIntervencao'};
function renderPlan(){fields.forEach(f=>{const target=document.querySelector(`#${preview[f.dataset.plan]}`);if(target&&f.value.trim())target.textContent=f.value.trim();});}
fields.forEach(field=>{field.value=localStorage.getItem(`textozen-plan-${field.dataset.plan}`)||'';field.addEventListener('input',()=>{localStorage.setItem(`textozen-plan-${field.dataset.plan}`,field.value);renderPlan();});});
renderPlan();
document.querySelector('#clearPlan')?.addEventListener('click',()=>{if(confirm('Limpar todo o planejamento?')){fields.forEach(f=>{f.value='';localStorage.removeItem(`textozen-plan-${f.dataset.plan}`);});location.reload();}});
document.querySelector('#copyPlan')?.addEventListener('click',async()=>{const values=Object.fromEntries(fields.map(f=>[f.dataset.plan,f.value||'—']));const text=`TEMA: ${values.tema}\nTESE: ${values.tese}\nARGUMENTO 1: ${values.arg1}\nARGUMENTO 2: ${values.arg2}\nREPERTÓRIO: ${values.repertorio}\nINTERVENÇÃO: ${values.intervencao}`;await navigator.clipboard.writeText(text);document.querySelector('#saveNote').textContent='✓ Estrutura copiada';});
