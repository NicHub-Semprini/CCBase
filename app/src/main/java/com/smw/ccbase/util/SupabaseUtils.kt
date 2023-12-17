package com.smw.ccbase.util

import com.smw.ccbase.BuildConfig
import com.smw.ccbase.model.BaseCard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.runBlocking

object SupabaseUtils {

    const val DEFAULT_USER: String = "";
    const val DEFAULT_PWD: String = "";

    private const val WILDCARD: String = "%"
    private const val CARD_TABLE: String = "card_vw";

    var loginEmail: String = DEFAULT_USER
    var loginPassword: String = DEFAULT_PWD
    var userLogged = false
    var dbOnline = false

//    private val client: SupabaseClient = createSupabaseClient(
//        "https://dtzehiyqjjdwhdewovxf.supabase.co",
//        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR0emVoaXlxampkd2hkZXdvdnhmIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTY2NzI3NTQsImV4cCI6MjAxMjI0ODc1NH0.heuZBxyjLtOPQSqkEh10S6Nnot9nhBrCfj2LHEBTPKM"
//    ) {
//        install(Postgrest) {
//            defaultSerializer = SerializerUtils.JsonSerializer
//        }
//        install(GoTrue) {
//            alwaysAutoRefresh = true
//        }
//    }

    private val client: SupabaseClient = createSupabaseClient(
        BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest) {
            defaultSerializer = SerializerUtils.JsonSerializer
        }
        install(GoTrue) {
            alwaysAutoRefresh = true
        }
    }

    fun getClient(): SupabaseClient {
        return this.client
    }

    fun login() {
        runBlocking {
            try {
                client.postgrest.close()
                client.gotrue.logout()
                client.gotrue.loginWith(Email) {
                    email = loginEmail
                    password = loginPassword
                }
                userLogged = true
                dbOnline = true
            } catch (e: RestException) {
                println(e.toString())
                userLogged = false
                dbOnline = true
            } catch (e: HttpRequestTimeoutException) {
                println(e.toString())
                userLogged = false
                dbOnline = false
            } catch (e: HttpRequestException) {
                println(e.toString())
                userLogged = false
                dbOnline = false
            }
        }
    }

    suspend fun searchCards(
        name: String? = null, setCode: String? = null, setNumber: String? = null
    ): List<BaseCard> {
        return client.postgrest.from(CARD_TABLE).select {
            ilike("name", sanitizeString(name))
            ilike("set_code", sanitizeString(setCode))
            ilike("set_number", sanitizeString(setNumber))
        }.decodeList()
    }

    private fun sanitizeString(string: String?): String {
        return if (string != null) {
            "${WILDCARD}${string}${WILDCARD}"
        } else {
            WILDCARD
        }
    }
}