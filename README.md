# NotiAssist (progetto personale)

App Android che:
- legge le notifiche come Notification Listener
- chiede una bozza a ChatGPT
- ti fa confermare e invia tramite RemoteInput

## Avvio rapido
1. Apri in Android Studio, *Sync Now* (se il Gradle wrapper manca, rigeneralo da **Gradle > build setup > wrapper**).
2. Esegui su dispositivo → abilita **Impostazioni > Notifiche > Accesso alle notifiche** per NotiAssist.
3. Metti la tua **OpenAI API Key** nella schermata principale.
4. Ricevi un messaggio su WhatsApp/Telegram/SMS → l’app propone una bozza → confermi → invia.

## GitHub Actions
Il workflow in `.github/workflows/android.yml` crea l'**APK debug** ad ogni push e, se tagghi `v*`, produce una Release con APK **firmato** (serve configurare i Secret).

## Note
- Questo repository contiene un placeholder per il Gradle wrapper. Consigliato rigenerarlo in locale e **committarlo** prima di usare le Actions.
