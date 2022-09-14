package dev.patrickgold.florisboard.ime.language

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.patrickgold.florisboard.ime.core.PrefHelper

object SpinnerLanguageManager {

    private var langList: List<LangModel>? = null


    fun getInputLangCode(prefHelper: PrefHelper): String =
        langList?.get(prefHelper.translateLang.inputLangCode)?.code.toString()

    fun getOutputLangCode(prefHelper: PrefHelper): String =
        langList?.get(prefHelper.translateLang.outputLangCode)?.code.toString()

    fun getSpecificLangCode(pos: Int) {
        langList?.get(pos)?.code.toString()
    }


    fun getLangList(context: Context) =
        langList ?: fetchLanguages(context)

    private fun fetchLanguages(context: Context): List<LangModel>? {
        /**
         * if you change json name update the path here as well.
         */
        val languageJson: String = try {
            context.assets.open("ime/languages/languages.json").bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            null
        } ?: return null


        return Moshi.Builder().run {
            add(KotlinJsonAdapterFactory()).build() // building moshi
                .adapter<List<LangModel>>( // adding adapter
                    Types.newParameterizedType( // setting type
                        List::class.java,
                        LangModel::class.java
                    )
                )
                .fromJson(languageJson).also {
                    langList = it
                }  // returning list of model
        }
    }
}