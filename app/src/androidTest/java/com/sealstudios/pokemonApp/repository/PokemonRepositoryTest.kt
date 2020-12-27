package com.sealstudios.pokemonApp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.dao.*
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws


@RunWith(AndroidJUnit4::class)
class PokemonSearchAndFilterTests {

    @get: Rule
    val scope = CoroutineTestRule()

    private lateinit var pokemonDao: PokemonDao
    private lateinit var speciesDao: PokemonSpeciesDao
    private lateinit var speciesJoinDao: PokemonSpeciesJoinDao

    private lateinit var pokemonTypeDao: PokemonTypeDao
    private lateinit var pokemonTypeJoinDao: PokemonTypeJoinDao

    private lateinit var db: PokemonRoomDatabase

    private val emptySearch = "%%"
    private val searchBulbasaur = "%b%"

    private val bulbasaurSpeciesID = 1
    private val squirtleSpeciesID = 2
    private val charmanderSpeciesID = 3
    private val charizardSpeciesID = 4
    private val pidgeySpeciesID = 5
    private val moltresSpeciesID = 6

    private val bulbasaurID = 1
    private val squirtleID = 2
    private val charmanderID = 3
    private val charizardID = 4
    private val pidgeyID = 5
    private val moltresID = 6

    private val grassTypeID = 1
    private val poisonTypeID = 2
    private val fireTypeID = 3
    private val waterTypeID = 4
    private val flyingTypeID = 5

    private val allPokemonTypes = listOf(
        "normal",
        "water",
        "fire",
        "grass",
        "electric",
        "ice",
        "fighting",
        "poison",
        "ground",
        "flying",
        "psychic",
        "bug",
        "rock",
        "ghost",
        "dark",
        "dragon",
        "steel",
        "fairy",
        "unknown",
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PokemonRoomDatabase::class.java,
        ).setTransactionExecutor(Executors.newSingleThreadExecutor())
            .allowMainThreadQueries()
            .build()
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
    fun writePokemonThenReadPokemonByName() {

        val bulbasaur = bulbasaur()

        runBlocking {
            insertBulbasaur()

            val pokemonMatchingSearch =
                pokemonDao.getSinglePokemonByName("bulbasaur").getValueBlocking()

            assertThat(pokemonMatchingSearch?.name, equalTo(bulbasaur.pokemon.name))
        }
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonThenSearchPokemonByName() {

        val bulbasaur = bulbasaur()
        val squirtle = squirtle()
        val charmander = charmander()

        runBlocking {

            insertBulbasaur()
            insertSquirtle()
            insertCharmander()

            val pokemonEmptySearch =
                pokemonDao.searchPokemon(emptySearch).getValueBlocking()

            assertThat(pokemonEmptySearch?.size, equalTo(3))
            assertThat(pokemonEmptySearch!![0].pokemon.name, equalTo(bulbasaur.pokemon.name))
            assertThat(pokemonEmptySearch[1].pokemon.name, equalTo(squirtle.pokemon.name))
            assertThat(pokemonEmptySearch[2].pokemon.name, equalTo(charmander.pokemon.name))

            val pokemonMatchingSearch =
                pokemonDao.searchPokemon(searchBulbasaur).getValueBlocking()

            assertThat(pokemonMatchingSearch?.size, equalTo(1))
            assertThat(pokemonMatchingSearch!![0].pokemon.name, equalTo(bulbasaur.pokemon.name))

            assertThat(pokemonMatchingSearch[0].types.size, equalTo(2))
            assertThat(pokemonMatchingSearch[0].types[0].name, equalTo(bulbasaur.types[1].name))
            assertThat(pokemonMatchingSearch[0].types[1].name, equalTo(bulbasaur.types[0].name))

            assertThat(
                pokemonMatchingSearch[0].species?.species,
                equalTo(bulbasaur.species!!.species)
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonThenFilterByType() {

        insertPokemonForFilterTest()

        val emptySearchSingleFilter =
            pokemonDao.searchAndFilterPokemon(search = emptySearch, filters = listOf("grass"))
                .getValueBlocking()

        assertThat(emptySearchSingleFilter?.size, equalTo(1))

        assertThat(emptySearchSingleFilter!![0].pokemon.name, equalTo("bulbasaur"))

        val emptySearchAllFilters =
            pokemonDao.searchAndFilterPokemon(search = emptySearch, filters = allPokemonTypes)
                .getValueBlocking()

        assertThat(emptySearchAllFilters?.size, equalTo(6))

        val emptySearchMultiFilter =
            pokemonDao.searchAndFilterPokemon(
                search = emptySearch,
                filters = listOf("fire", "flying")
            )
                .getValueBlocking()

        assertThat(emptySearchMultiFilter?.size, equalTo(4))

        assertThat(emptySearchMultiFilter!![0].pokemon.name, equalTo("charizard"))
        assertThat(emptySearchMultiFilter[1].pokemon.name, equalTo("moltres"))
        assertThat(emptySearchMultiFilter[2].pokemon.name, equalTo("charmander"))
        assertThat(emptySearchMultiFilter[3].pokemon.name, equalTo("pidgey"))

        val searchBulbasaurSingleFilter =
            pokemonDao.searchAndFilterPokemon(search = searchBulbasaur, filters = listOf("grass"))
                .getValueBlocking()

        assertThat(searchBulbasaurSingleFilter?.size, equalTo(1))

        assertThat(searchBulbasaurSingleFilter!![0].pokemon.name, equalTo("bulbasaur"))

        val searchRMultiFilter =
            pokemonDao.searchAndFilterPokemon(search = "%r%", filters = listOf("fire", "flying"))
                .getValueBlocking()

        assertThat(searchRMultiFilter?.size, equalTo(3))

        assertThat(
            searchRMultiFilter!![0].pokemon.name,
            equalTo("charizard")
        ) // matches 2 filters and ID is 4
        assertThat(
            searchRMultiFilter[1].pokemon.name,
            equalTo("moltres")
        ) // matches 2 filters and ID is 6
        assertThat(
            searchRMultiFilter[2].pokemon.name,
            equalTo("charmander")
        ) // matches one filter and ID is 3

        val searchAndMultiFilterNoResults =
            pokemonDao.searchAndFilterPokemon(search = "%ex%", filters = listOf("fire", "flying"))
                .getValueBlocking()

        assertThat(searchAndMultiFilterNoResults?.size, equalTo(0))

    }

    private fun insertPokemonForFilterTest() = runBlocking {
        insertBulbasaur()
        insertSquirtle()
        insertCharmander()
        insertCharizard()
        insertMoltres()
        insertPidgey()
    }

    private fun insertBulbasaur() = runBlocking {

        val bulbasaur = bulbasaur()
        val grassJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = grassTypeID)
        val poisonJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = poisonTypeID)
        val bulbasaurSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = bulbasaurID, species_id = bulbasaurSpeciesID)

        pokemonDao.insertPokemon(bulbasaur.pokemon)

        speciesDao.insertSpecies(bulbasaur.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(bulbasaurSpeciesJoin)

        pokemonTypeDao.insertPokemonType(pokemonType = bulbasaur.types[0])
        pokemonTypeDao.insertPokemonType(pokemonType = bulbasaur.types[1])
        pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)
        pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
    }

    private fun insertSquirtle() = runBlocking {

        val squirtle = squirtle()
        val squirtleSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = squirtleID, species_id = squirtleSpeciesID)
        val waterJoin = PokemonTypesJoin(pokemon_id = squirtleID, type_id = waterTypeID)

        pokemonDao.insertPokemon(squirtle.pokemon)

        speciesDao.insertSpecies(squirtle.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(squirtleSpeciesJoin)

        pokemonTypeDao.insertPokemonType(pokemonType = squirtle.types[0])
        pokemonTypeJoinDao.insertPokemonTypeJoin(waterJoin)

    }

    private fun insertCharmander() = runBlocking {

        val charmander = charmander()
        val fireJoin = PokemonTypesJoin(pokemon_id = charmanderID, type_id = fireTypeID)
        val charmanderSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = charmanderID, species_id = charmanderSpeciesID)

        pokemonDao.insertPokemon(charmander.pokemon)
        speciesDao.insertSpecies(charmander.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(charmanderSpeciesJoin)
        pokemonTypeDao.insertPokemonType(pokemonType = charmander.types[0])
        pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)
    }

    private fun insertCharizard() = runBlocking {

        val charizard = charizard()
        val charizardSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = charizardID, species_id = charizardSpeciesID)

        val fireJoin = PokemonTypesJoin(pokemon_id = charizardID, type_id = fireTypeID)
        val flyingJoin = PokemonTypesJoin(pokemon_id = charizardID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(charizard.pokemon)

        speciesDao.insertSpecies(charizard.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(charizardSpeciesJoin)

        pokemonTypeDao.insertPokemonType(pokemonType = charizard.types[0])
        pokemonTypeDao.insertPokemonType(pokemonType = charizard.types[1])
        pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)
        pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun insertPidgey() = runBlocking {

        val pidgey = pidgey()
        val pidgeySpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = pidgeyID, species_id = pidgeySpeciesID)
        val flyingJoin = PokemonTypesJoin(pokemon_id = pidgeyID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(pidgey.pokemon)

        speciesDao.insertSpecies(pidgey.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(pidgeySpeciesJoin)

        pokemonTypeDao.insertPokemonType(pokemonType = pidgey.types[0])
        pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun insertMoltres() = runBlocking {

        val moltres = moltres()
        val moltresSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = moltresID, species_id = moltresSpeciesID)

        val fireJoin = PokemonTypesJoin(pokemon_id = moltresID, type_id = fireTypeID)
        val flyingJoin = PokemonTypesJoin(pokemon_id = moltresID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(moltres.pokemon)

        speciesDao.insertSpecies(moltres.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(moltresSpeciesJoin)

        pokemonTypeDao.insertPokemonType(pokemonType = moltres.types[0])
        pokemonTypeDao.insertPokemonType(pokemonType = moltres.types[1])
        pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)
        pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun bulbasaur(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = bulbasaurID, name = "bulbasaur"),
        species = PokemonSpecies(
            id = bulbasaurSpeciesID,
            species = "Seed pokemon",
            pokedexEntry = "There is a plant seed on its back right from the day this Pokémon is born. The seed slowly grows larger."
        ),
        types = listOf(
            PokemonType(id = poisonTypeID, name = "poison", slot = 1),
            PokemonType(id = grassTypeID, name = "grass", slot = 2)
        )
    )

    private fun squirtle(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = squirtleID, name = "squirtle"),
        species = PokemonSpecies(
            id = squirtleSpeciesID,
            species = "Turtle pokemon",
            pokedexEntry = "Small shell pokemon"
        ),
        types = listOf(PokemonType(id = waterTypeID, name = "water", slot = 1))
    )

    private fun charmander(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = charmanderID, name = "charmander"),
        species = PokemonSpecies(
            id = charmanderSpeciesID,
            species = "Fire lizard pokemon",
            pokedexEntry = "If the flame on this pokemon's tail goes out it will die"
        ),
        types = listOf(PokemonType(id = fireTypeID, name = "fire", slot = 1))
    )

    private fun charizard(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = charizardID, name = "charizard"),
        species = PokemonSpecies(
            id = charizardSpeciesID,
            species = "Fire flying lizard pokemon",
            pokedexEntry = "Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally"
        ),
        types = listOf(
            PokemonType(id = fireTypeID, name = "fire", slot = 1),
            PokemonType(id = flyingTypeID, name = "flying", slot = 2)
        )
    )

    private fun moltres(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = moltresID, name = "moltres"),
        species = PokemonSpecies(
            id = moltresSpeciesID,
            species = "Fire bird pokemon",
            pokedexEntry = "Known as the legendary bird of fire. Every flap of its wings creates a dazzling flash of flames"
        ),
        types = listOf(
            PokemonType(id = fireTypeID, name = "fire", slot = 1),
            PokemonType(id = flyingTypeID, name = "flying", slot = 2)
        )
    )

    private fun pidgey(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = pidgeyID, name = "pidgey"),
        species = PokemonSpecies(
            id = pidgeySpeciesID,
            species = "Bird pokemon",
            pokedexEntry = "Pidgey is a Flying Pokémon. Among all the Flying Pokémon, it is the gentlest and easiest to capture. A perfect target for the beginning Pokémon Trainer to test his Pokémon's skills."
        ),
        types = listOf(PokemonType(id = flyingTypeID, name = "flying", slot = 1))
    )

}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.getValueBlocking(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val innerObserver = Observer<T> {
        value = it
        latch.countDown()
    }
    runBlocking(context = Dispatchers.Main) {
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