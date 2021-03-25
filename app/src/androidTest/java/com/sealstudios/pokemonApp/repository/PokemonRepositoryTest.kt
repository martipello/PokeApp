package com.sealstudios.pokemonApp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.joins.TypesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.dao.*
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
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
    private lateinit var speciesDao: SpeciesDao
    private lateinit var speciesJoinDao: SpeciesJoinDao

    private lateinit var typeDao: TypeDao
    private lateinit var typeJoinDao: TypeJoinDao

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
        typeDao = db.pokemonTypeDao()
        typeJoinDao = db.pokemonTypeJoinDao()
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
        val grassJoin = TypesJoin(pokemon_id = bulbasaurID, type_id = grassTypeID)
        val poisonJoin = TypesJoin(pokemon_id = bulbasaurID, type_id = poisonTypeID)
        val bulbasaurSpeciesJoin =
            SpeciesJoin(pokemon_id = bulbasaurID, species_id = bulbasaurSpeciesID)

        pokemonDao.insertPokemon(bulbasaur.pokemon)

        speciesDao.insertSpecies(bulbasaur.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(bulbasaurSpeciesJoin)

        typeDao.insertPokemonType(type = bulbasaur.types[0])
        typeDao.insertPokemonType(type = bulbasaur.types[1])
        typeJoinDao.insertPokemonTypeJoin(grassJoin)
        typeJoinDao.insertPokemonTypeJoin(poisonJoin)
    }

    private fun insertSquirtle() = runBlocking {

        val squirtle = squirtle()
        val squirtleSpeciesJoin =
            SpeciesJoin(pokemon_id = squirtleID, species_id = squirtleSpeciesID)
        val waterJoin = TypesJoin(pokemon_id = squirtleID, type_id = waterTypeID)

        pokemonDao.insertPokemon(squirtle.pokemon)

        speciesDao.insertSpecies(squirtle.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(squirtleSpeciesJoin)

        typeDao.insertPokemonType(type = squirtle.types[0])
        typeJoinDao.insertPokemonTypeJoin(waterJoin)

    }

    private fun insertCharmander() = runBlocking {

        val charmander = charmander()
        val fireJoin = TypesJoin(pokemon_id = charmanderID, type_id = fireTypeID)
        val charmanderSpeciesJoin =
            SpeciesJoin(pokemon_id = charmanderID, species_id = charmanderSpeciesID)

        pokemonDao.insertPokemon(charmander.pokemon)
        speciesDao.insertSpecies(charmander.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(charmanderSpeciesJoin)
        typeDao.insertPokemonType(type = charmander.types[0])
        typeJoinDao.insertPokemonTypeJoin(fireJoin)
    }

    private fun insertCharizard() = runBlocking {

        val charizard = charizard()
        val charizardSpeciesJoin =
            SpeciesJoin(pokemon_id = charizardID, species_id = charizardSpeciesID)

        val fireJoin = TypesJoin(pokemon_id = charizardID, type_id = fireTypeID)
        val flyingJoin = TypesJoin(pokemon_id = charizardID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(charizard.pokemon)

        speciesDao.insertSpecies(charizard.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(charizardSpeciesJoin)

        typeDao.insertPokemonType(type = charizard.types[0])
        typeDao.insertPokemonType(type = charizard.types[1])
        typeJoinDao.insertPokemonTypeJoin(fireJoin)
        typeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun insertPidgey() = runBlocking {

        val pidgey = pidgey()
        val pidgeySpeciesJoin =
            SpeciesJoin(pokemon_id = pidgeyID, species_id = pidgeySpeciesID)
        val flyingJoin = TypesJoin(pokemon_id = pidgeyID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(pidgey.pokemon)

        speciesDao.insertSpecies(pidgey.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(pidgeySpeciesJoin)

        typeDao.insertPokemonType(type = pidgey.types[0])
        typeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun insertMoltres() = runBlocking {

        val moltres = moltres()
        val moltresSpeciesJoin =
            SpeciesJoin(pokemon_id = moltresID, species_id = moltresSpeciesID)

        val fireJoin = TypesJoin(pokemon_id = moltresID, type_id = fireTypeID)
        val flyingJoin = TypesJoin(pokemon_id = moltresID, type_id = flyingTypeID)

        pokemonDao.insertPokemon(moltres.pokemon)

        speciesDao.insertSpecies(moltres.species!!)
        speciesJoinDao.insertPokemonSpeciesJoin(moltresSpeciesJoin)

        typeDao.insertPokemonType(type = moltres.types[0])
        typeDao.insertPokemonType(type = moltres.types[1])
        typeJoinDao.insertPokemonTypeJoin(fireJoin)
        typeJoinDao.insertPokemonTypeJoin(flyingJoin)
    }

    private fun bulbasaur(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = bulbasaurID, name = "bulbasaur"),
        species = Species(
            id = bulbasaurSpeciesID,
            species = "Seed pokemon",
            pokedexEntry = "There is a plant seed on its back right from the day this Pokémon is born. The seed slowly grows larger."
        ),
        types = listOf(
            Type(id = poisonTypeID, name = "poison"),
            Type(id = grassTypeID, name = "grass")
        )
    )

    private fun squirtle(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = squirtleID, name = "squirtle"),
        species = Species(
            id = squirtleSpeciesID,
            species = "Turtle pokemon",
            pokedexEntry = "Small shell pokemon"
        ),
        types = listOf(Type(id = waterTypeID, name = "water"))
    )

    private fun charmander(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = charmanderID, name = "charmander"),
        species = Species(
            id = charmanderSpeciesID,
            species = "Fire lizard pokemon",
            pokedexEntry = "If the flame on this pokemon's tail goes out it will die"
        ),
        types = listOf(Type(id = fireTypeID, name = "fire"))
    )

    private fun charizard(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = charizardID, name = "charizard"),
        species = Species(
            id = charizardSpeciesID,
            species = "Fire flying lizard pokemon",
            pokedexEntry = "Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally"
        ),
        types = listOf(
            Type(id = fireTypeID, name = "fire"),
            Type(id = flyingTypeID, name = "flying")
        )
    )

    private fun moltres(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = moltresID, name = "moltres"),
        species = Species(
            id = moltresSpeciesID,
            species = "Fire bird pokemon",
            pokedexEntry = "Known as the legendary bird of fire. Every flap of its wings creates a dazzling flash of flames"
        ),
        types = listOf(
            Type(id = fireTypeID, name = "fire"),
            Type(id = flyingTypeID, name = "flying")
        )
    )

    private fun pidgey(): PokemonWithTypesAndSpecies = PokemonWithTypesAndSpecies(
        pokemon = Pokemon(id = pidgeyID, name = "pidgey"),
        species = Species(
            id = pidgeySpeciesID,
            species = "Bird pokemon",
            pokedexEntry = "Pidgey is a Flying Pokémon. Among all the Flying Pokémon, it is the gentlest and easiest to capture. A perfect target for the beginning Pokémon Trainer to test his Pokémon's skills."
        ),
        types = listOf(Type(id = flyingTypeID, name = "flying"))
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