// =========================
// NotiAssist Web – Script
// =========================

const els = {
  apiKey: document.getElementById('apiKey'),
  btnSaveKey: document.getElementById('btnSaveKey'),
  btnClearKey: document.getElementById('btnClearKey'),
  message: document.getElementById('message'),
  tone: document.getElementById('tone'),
  maxLen: document.getElementById('maxLen'),
  model: document.getElementById('model'),
  btnSuggest: document.getElementById('btnSuggest'),
  btnClear: document.getElementById('btnClear'),
  output: document.getElementById('output'),
  btnCopy: document.getElementById('btnCopy')
};

// --- Persistence API key (localStorage, opzionale)
(function initKey() {
  const saved = localStorage.getItem('NOTIASSIST_OPENAI_KEY');
  if (saved) els.apiKey.value = saved;
})();
els.btnSaveKey.addEventListener('click', () => {
  const k = (els.apiKey.value || '').trim();
  if (!k) return alert('Inserisci una API key valida (sk-...)');
  localStorage.setItem('NOTIASSIST_OPENAI_KEY', k);
  alert('API key salvata localmente (puoi rimuoverla con "Rimuovi").');
});
els.btnClearKey.addEventListener('click', () => {
  localStorage.removeItem('NOTIASSIST_OPENAI_KEY');
  els.apiKey.value = '';
  alert('API key rimossa dal browser.');
});

// --- Helpers UI
els.btnClear.addEventListener('click', () => {
  els.message.value = '';
  els.output.value = '';
});
els.btnCopy.addEventListener('click', async () => {
  if (!els.output.value.trim()) return;
  await navigator.clipboard.writeText(els.output.value);
  alert('Copiato negli appunti!');
});

// --- Prompt builder
function buildSystemPrompt(tone) {
  const tones = {
    neutro: 'tono neutro, educato',
    cordiale: 'tono cordiale e amichevole, senza eccessi',
    formale: 'tono formale e conciso',
    telegrafico: 'risposta telegrafica, essenziale'
  };
  return `Sei un assistente che scrive risposte brevi in italiano (${tones[tone] || 'tono neutro'}).
Regole:
- max 1-${Math.max(1, parseInt(els.maxLen.value||'2',10))} frasi
- niente emoji, niente link
- se il messaggio è ambiguo, chiedi un chiarimento cortese in UNA sola frase
- vietato inventare dettagli.`;
}

// --- Call OpenAI (Chat Completions)
// Nota: chiamata diretta dal browser; la tua API key rimane sul client.
// Per produzione è consigliabile un proxy lato server.
async function fetchReply() {
  const apiKey = (el
