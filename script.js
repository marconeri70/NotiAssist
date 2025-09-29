function sendMessage() {
  const msg = document.getElementById("message").value.trim();
  const output = document.getElementById("output");

  if (msg === "") {
    output.innerHTML = "<p style='color:red'>⚠️ Inserisci un messaggio!</p>";
    return;
  }

  // Demo: mostra solo eco
  output.innerHTML = `
    <p><b>Messaggio ricevuto:</b> ${msg}</p>
    <p><i>Risposta simulata:</i> Grazie per il tuo messaggio! (demo)</p>
  `;
}
