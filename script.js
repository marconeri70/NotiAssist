// =========================
// NotiAssist Web – Script
// =========================

function $id(id){ return document.getElementById(id); }

const els = {
  apiKey: $id('apiKey'),
  keyStatus: $id('keyStatus'),
  btnSaveKey: $id('btnSaveKey'),
  btnClearKey: $id('btnClearKey'),
  message: $id('message'),
  tone: $id('tone'),
  maxLen: $id('maxLen'),
  model: $id('model'),
  btnSuggest: $id('btnSuggest'),
  btnClear: $id('btnClear'),
  output: $id('output'),
  btnCopy: $id('btnCopy')
};

// --- Persistence API key (localStorage, opzionale)
(function initKey() {
  try {
    const saved = localStorage.getItem('NOTIASSIST_OPENAI_KEY');
    if (saved) {
      els.apiKey.value = saved;
      els.keyStatus.textContent = 'Chiave caricata da memoria locale.';
    } else {
      els.keyStatus.textContent = 'Nessuna chiave salvata.';
    }
  } catch {
    els.keyStatus.textContent = 'Impossibile accedere al localStorage.';
  }
})();

els.btnSaveKey.addEventListener('click', () => {
  try {
    const k = (els.apiKey.value || '').trim();
    if (!k) {
      els.keyStatus.textContent = 'Inserisci una API key valida (sk-...).';
      return;
    }
    localStorage.setItem('NOTIASSIST_OPENAI_KEY', k);
    els.keyStatus.textContent = '✅ API key salvata localmente.';
  } catch (e) {
    console.error(e);
    els.keyStatus.textContent = 'Errore nel salvataggio della chiave.';
  }
});

els.btnClearKey.addEventListener('click', () => {
  try {
    localStorage.removeItem('NOTIASSIST_OPENAI_KEY');
    els.apiKey.value = '';
    els.keyStatus.textContent = '🗑️ API key rimossa dal browser.';
  } catch (e) {
    console.error(e);
    els.keyStatus.textContent = 'Errore nella rimozione della chiave.';
  }
});

// --- Helpers UI
els.btnClear.addEventListener('click', () => {
  els.message.value = '';
  els.output.value = '';
});
els.btnCopy.addEventListener('click', async () => {
  if (!els.output.value.trim()) return;
  try {
    await navigator.clipboard.writeText(els.output.value);
    alert('Copiato negli appunti!');
  } catch {}
});

// --- Prompt builder
function buildSystemPrompt(tone) {
  const tones = {
    neutro: 'tono neutro, educato',
    cordiale: 'tono cordiale e amichevole, senza eccessi',
    formale: 'tono formale e conciso',
    telegrafico: 'risposta telegrafica, essenziale'
  };
  const n = Math.max(1, parseInt(els.maxLen.value || '2', 10));
  return `Sei un assistente che scrive risposte brevi in italiano (${tones[tone] || 'tono neutro'}).
Regole:
- max 1-${n} frasi
- niente emoji, niente link
- se il messaggio è ambiguo, chiedi un chiarimento cortese in UNA sola frase
- vietato inventare dettagli.`;
}

// --- Call OpenAI (Chat Completions)
async function fetchReply() {
  const apiKey = (els.apiKey.value || '').trim();
  if (!apiKey) {
    alert('Inserisci la tua OpenAI API key.');
    return;
  }
  const msg = (els.message.value || '').trim();
  if (!msg) {
    alert('Inserisci un messaggio.');
    return;
  }

  els.btnSuggest.disabled = true;
  els.btnSuggest.textContent = 'Generazione…';

  const payload = {
    model: els.model.value,
    messages: [
      { role: 'system', content: buildSystemPrompt(els.tone.value) },
      { role: 'user', content: `Messaggio ricevuto:\n${msg}\n\nScrivi una risposta adeguata.` }
    ],
    temperature: 0.4
  };

  try {
    const res = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${apiKey}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const t = await res.text();
      throw new Error(`HTTP ${res.status}: ${t}`);
    }

    const data = await res.json();
    const content = data?.choices?.[0]?.message?.content?.trim() || '';
    els.output.value = content || '(nessun testo generato)';
  } catch (err) {
    console.error(err);
    els.output.value = `Errore: ${err.message}`;
  } finally {
    els.btnSuggest.disabled = false;
    els.btnSuggest.textContent = '✍️ Genera risposta';
  }
}

els.btnSuggest.addEventListener('click', fetchReply);

