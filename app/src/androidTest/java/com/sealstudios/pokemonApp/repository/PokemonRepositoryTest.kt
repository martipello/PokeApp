package com.sealstudios.pokemonApp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.dao.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineContext
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.security.auth.Subject
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws


@RunWith(AndroidJUnit4::class)
class SimplePokemonReadAndWriteTests {

    @get: Rule
    val scope = CoroutineTestRule()

    val subject = Subject()

    private lateinit var pokemonDao: PokemonDao
    private lateinit var speciesDao: PokemonSpeciesDao
    private lateinit var speciesJoinDao: PokemonSpeciesJoinDao

    private lateinit var pokemonTypeDao: PokemonTypeDao
    private lateinit var pokemonTypeJoinDao: PokemonTypeJoinDao

    private lateinit var db: PokemonRoomDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PokemonRoomDatabase::class.java
        ).build()
        pokemonDao = db.pokemonDao()
        speciesDao = db.pokemonSpeciesDao()
        speciesJoinDao = db.pokemonSpeciesJoinDao()
        pokemonTypeDao = db.pokemonTypeDao()
        pokemonTypeJoinDao = db.pokemonTypeJoinDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonAndReadPokemonByName() {
        val bulbasaur = Pokemon(name = "bulbasaur")
        with(subject) {
            scope.launch {
                pokemonDao.insertPokemon(bulbasaur)
            }
        }
        val pokemonMatchingSearch =
            pokemonDao.getSinglePokemonByName("bulbasaur").getValueBlocking(scope)
        assertThat(pokemonMatchingSearch?.name, equalTo(bulbasaur.name))
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonAndSearchPokemonByName() {
        val bulbasaur = Pokemon(name = "bulbasaur")
        with(subject) {
            scope.launch {
                pokemonDao.insertPokemon(bulbasaur)
            }
        }
        val pokemonMatchingSearch = pokemonDao.searchAllPokemon("%b%").getValueBlocking(scope)
        assertThat(pokemonMatchingSearch?.size, equalTo(1))
        assertThat(pokemonMatchingSearch!![0].name, equalTo(bulbasaur.name))
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonAndTypesAndSpeciesAndJoinsAndSearchPokemonWithTypesAndSpeciesByName() {
        val bulbasaur = Pokemon(id = 1, name = "bulbasaur")
        val poisonType = PokemonType(id = 1, name = "poison", slot = 1)
        val grassType = PokemonType(id = 2, name = "grass", slot = 2)
        val species = PokemonSpecies(
            id = 1,
            species = "Seed pokemon",
            pokedexEntry = "This pokemon is probably martins favourite after Raichu"
        )
        val speciesJoin = PokemonSpeciesJoin(pokemon_id = 1, species_id = 1)
        val poisonJoin = PokemonTypesJoin(pokemon_id = 1, type_id = 1)
        val grassJoin = PokemonTypesJoin(pokemon_id = 1, type_id = 2)

        with(subject) {
            scope.launch {
                pokemonDao.insertPokemon(bulbasaur)
                pokemonTypeDao.insertPokemonType(pokemonType = poisonType)
                pokemonTypeDao.insertPokemonType(pokemonType = grassType)
                speciesDao.insertSpecies(species)
                speciesJoinDao.insertPokemonSpeciesJoin(speciesJoin)
                pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
                pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)


                val pokemonMatchingSearch =
                    pokemonDao.searchAllPokemonWithTypesAndSpecies("%b%").getValueBlocking(scope)

                assertThat(pokemonMatchingSearch?.size, equalTo(1))

                assertThat(pokemonMatchingSearch!![0].pokemon.name, equalTo(bulbasaur.name))

                assertThat(pokemonMatchingSearch!![0].types.size, equalTo(2))
                assertThat(pokemonMatchingSearch!![0].types[0].name, equalTo(poisonType.name))
                assertThat(pokemonMatchingSearch!![0].types[1].name, equalTo(grassType.name))

                assertThat(pokemonMatchingSearch!![0].species?.species, equalTo(species.species))
            }
        }

    }
}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.getValueBlocking(scope: CoroutineScope): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val innerObserver = Observer<T> {
        value = it
        latch.countDown()
    }
    scope.launch(Dispatchers.Main) {
        observeForever(innerObserver)
    }
    latch.await(2, TimeUnit.SECONDS)
    return value
}

class CoroutineTestRule : TestRule, CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Unconfined

    override fun apply(
        base: Statement, description: Description?
    ) = object : Statement() {
        override fun evaluate() {
            println("before base.evaluate()")
            base.evaluate()
            println("after base.evaluate()")
            this@CoroutineTestRule.cancel() // cancels CoroutineScope
        }
    }
}