package cz.hlas

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast

class MainActivity : Activity() {

    private val aiPrefix = Regex("^\\s*(?:dotaz\\b|ai\\b|a\\.i\\.)[\\s,:.]*", RegexOption.IGNORE_CASE)

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val text = if (resultCode == Activity.RESULT_OK)
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        else null
        if (!text.isNullOrBlank()) {
            val dotaz = upravDotaz(text)
            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("streamlet://search?q=" + Uri.encode(dotaz)))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (e: ActivityNotFoundException) {
                toast("Streamlet není nainstalovaný")
            }
        }
        finishAndRemoveTask()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}package cz.hlas

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast

class MainActivity : Activity() {

    private val aiPrefix = Regex("^\\s*dotaz\\b[\\s,:.]*", RegexOption.IGNORE_CASE)

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val text = if (resultCode == Activity.RESULT_OK)
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        else null
        if (!text.isNullOrBlank()) {
            val dotaz = upravDotaz(text)
            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("streamlet://search?q=" + Uri.encode(dotaz)))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (e: ActivityNotFoundException) {
                toast("Streamlet není nainstalovaný")
            }
        }
        finishAndRemoveTask()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}