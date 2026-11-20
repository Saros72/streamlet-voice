package cz.hlas

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast

class MainActivity : Activity() {

    private val smartTubePackage = "org.smarttube.stable"

    private val aiPrefix = Regex("^\\s*(?:dotaz\\b|ai\\b|a\\.i\\.)[\\s,:.]*", RegexOption.IGNORE_CASE)
    private val youtubePrefix = Regex("^\\s*(?:youtube|you tube)\\b[\\s,:.]*", RegexOption.IGNORE_CASE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "cs-CZ")
            .putExtra(RecognizerIntent.EXTRA_PROMPT, "Co hledat?")
        try {
            startActivityForResult(i, 1)
        } catch (e: ActivityNotFoundException) {
            toast("Rozpoznávání řeči není dostupné")
            finish()
        }
    }

    private fun upravDotaz(text: String): String {
        val m = aiPrefix.find(text) ?: return text.trim()
        val zbytek = text.substring(m.range.last + 1).trim()
        return if (zbytek.isEmpty()) text.trim() else "?" + zbytek
    }

    private fun hledejVeStreamletu(dotaz: String) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("streamlet://search?q=" + Uri.encode(dotaz)))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            toast("Streamlet není nainstalovaný")
        }
    }

    private fun hledejVeSmartTube(dotaz: String) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("youtube://search?query=" + Uri.encode(dotaz)))
                    .setPackage(smartTubePackage)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            toast("SmartTube není nainstalovaný")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val text = if (resultCode == Activity.RESULT_OK)
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        else null
        if (!text.isNullOrBlank()) {
            val yt = youtubePrefix.find(text)
            val ytDotaz = yt?.let { text.substring(it.range.last + 1).trim() }
            if (!ytDotaz.isNullOrEmpty()) {
                hledejVeSmartTube(ytDotaz)
            } else {
                hledejVeStreamletu(upravDotaz(text))
            }
        }
        finishAndRemoveTask()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}