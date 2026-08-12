const text=document.querySelector('#essayText');
if(text){const count=()=>document.querySelector('#wordCount').textContent=text.value.trim()?text.value.trim().split(/\s+/).length:0;text.addEventListener('input',count);count();}
const form=document.querySelector('#essayForm');
if(form)form.addEventListener('submit',()=>{const b=document.querySelector('#submitEssay');b.disabled=true;b.textContent='✦ Analisando...';});

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
