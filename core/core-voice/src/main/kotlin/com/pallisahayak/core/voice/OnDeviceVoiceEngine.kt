package com.pallisahayak.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.pallisahayak.core.common.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class OnDeviceVoiceEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languageMapper: LanguageMapper,
) : VoiceEngine {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    init {
        tts = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    override suspend fun speechToText(audio: ByteArray, language: String): Result<String> {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return Result.Error(Exception("Speech recognition not available on this device"))
        }

        return suspendCancellableCoroutine { cont ->
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageMapper.toBcp47(language))
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val transcript = matches?.firstOrNull()
                    if (transcript != null) {
                        cont.resume(Result.Success(transcript))
                    } else {
                        cont.resume(Result.Error(Exception("No speech recognized")))
                    }
                    recognizer.destroy()
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                        else -> "Speech recognition error: $error"
                    }
                    cont.resume(Result.Error(Exception(message)))
                    recognizer.destroy()
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            recognizer.startListening(intent)

            cont.invokeOnCancellation {
                recognizer.cancel()
                recognizer.destroy()
            }
        }
    }

    override suspend fun textToSpeech(text: String, language: String): Result<ByteArray> {
        if (!ttsReady || tts == null) {
            return Result.Error(Exception("TTS not initialized"))
        }

        val locale = languageMapper.toLocale(language)
        val langResult = tts!!.setLanguage(locale)
        if (langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts!!.setLanguage(Locale("hi", "IN"))
        }

        return suspendCancellableCoroutine { cont ->
            val tempFile = File.createTempFile("tts_", ".wav", context.cacheDir)
            val utteranceId = "tts_${System.currentTimeMillis()}"

            tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        val audioBytes = tempFile.readBytes()
                        tempFile.delete()
                        cont.resume(Result.Success(audioBytes))
                    }
                }

                override fun onError(id: String?) {
                    tempFile.delete()
                    cont.resume(Result.Error(Exception("TTS synthesis failed")))
                }

                @Deprecated("Deprecated in API")
                override fun onStart(id: String?) {}
            })

            tts!!.synthesizeToFile(text, null, tempFile, utteranceId)

            cont.invokeOnCancellation {
                tts?.stop()
                tempFile.delete()
            }
        }
    }

    override suspend fun voiceQuery(audio: ByteArray, language: String): Result<VoiceQueryResult> {
        return Result.Error(Exception("Voice query requires network — use offline cache"))
    }

    override fun isAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(context) && ttsReady
}
