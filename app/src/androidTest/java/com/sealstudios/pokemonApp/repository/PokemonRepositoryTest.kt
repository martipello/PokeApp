package com.sealstudios.pokemonApp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
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
import java.util.concurrent.Executors
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
    fun writePokemonAndReadPokemonByName() {
        val bulbasaur = Pokemon(name = "bulbasaur")
        runBlocking {
            pokemonDao.insertPokemon(bulbasaur)
        }
        runBlocking {
            val pokemonMatchingSearch =
                pokemonDao.getSinglePokemonByName("bulbasaur").getValueBlocking(scope)

            assertThat(pokemonMatchingSearch?.name, equalTo("bulbasaur"))
        }
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonAndSearchPokemonByName() {
        val bulbasaur = Pokemon(name = "bulbasaur")
        scope.launch {
            pokemonDao.insertPokemon(bulbasaur)
        }
        runBlocking {
            val pokemonMatchingSearch =
                pokemonDao.searchAllPokemon("%b%").getValueBlocking(scope)

            assertThat(pokemonMatchingSearch?.size, equalTo(1))
            assertThat(pokemonMatchingSearch!![0].name, equalTo(bulbasaur.name))
        }
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

        runBlocking {
            pokemonDao.insertPokemon(bulbasaur)
            pokemonTypeDao.insertPokemonType(pokemonType = poisonType)
            pokemonTypeDao.insertPokemonType(pokemonType = grassType)
            speciesDao.insertSpecies(species)
            speciesJoinDao.insertPokemonSpeciesJoin(speciesJoin)
            pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
            pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)
        }

        runBlocking {

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

    @Test
    @Throws(Exception::class)
    fun writePokemonAndTypesAndSpeciesAndJoinsThenSearchAndFilterPokemon() {
        val bulbasaurSpeciesID = 1
        val squirtleSpeciesID = 2
        val charmanderSpeciesID = 3

        val bulbasaurID = 3
        val squirtleID = 2
        val charmanderID = 1

        val grassTypeID = 1
        val poisonTypeID = 2
        val fireTypeID = 3
        val waterTypeID = 4

        val charmander = Pokemon(id = charmanderID, name = "charmander")
        val squirtle = Pokemon(id = squirtleID, name = "squirtle")
        val bulbasaur = Pokemon(id = bulbasaurID, name = "bulbasaur")

        val poisonType = PokemonType(id = poisonTypeID, name = "poison", slot = 1)
        val grassType = PokemonType(id = grassTypeID, name = "grass", slot = 2)
        val fireType = PokemonType(id = fireTypeID, name = "fire", slot = 1)
        val waterType = PokemonType(id = waterTypeID, name = "water", slot = 1)

        val speciesBulbasaur = PokemonSpecies(
            id = bulbasaurSpeciesID,
            species = "Seed pokemon",
            pokedexEntry = "This pokemon is probably martins favourite after Raichu"
        )
        val speciesSquirtle = PokemonSpecies(
            id = squirtleSpeciesID,
            species = "Turtle pokemon",
            pokedexEntry = "Small shell pokemon"
        )
        val speciesCharmander = PokemonSpecies(
            id = charmanderSpeciesID,
            species = "Fire lizard pokemon",
            pokedexEntry = "If the flame on this pokemon's tail goes out it will die"
        )

        val bulbasaurSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = bulbasaurID, species_id = bulbasaurSpeciesID)
        val charmanderSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = charmanderID, species_id = charmanderSpeciesID)
        val squirtleSpeciesJoin =
            PokemonSpeciesJoin(pokemon_id = squirtleID, species_id = squirtleSpeciesID)

        val poisonJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = poisonTypeID)
        val grassJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = grassTypeID)
        val waterJoin = PokemonTypesJoin(pokemon_id = squirtleID, type_id = waterTypeID)
        val fireJoin = PokemonTypesJoin(pokemon_id = charmanderID, type_id = fireTypeID)

        runBlocking {

            pokemonDao.insertPokemon(bulbasaur)
            pokemonDao.insertPokemon(squirtle)
            pokemonDao.insertPokemon(charmander)

            pokemonTypeDao.insertPokemonType(pokemonType = poisonType)
            pokemonTypeDao.insertPokemonType(pokemonType = grassType)
            pokemonTypeDao.insertPokemonType(pokemonType = fireType)
            pokemonTypeDao.insertPokemonType(pokemonType = waterType)

            speciesDao.insertSpecies(speciesBulbasaur)
            speciesDao.insertSpecies(speciesCharmander)
            speciesDao.insertSpecies(speciesSquirtle)

            speciesJoinDao.insertPokemonSpeciesJoin(bulbasaurSpeciesJoin)
            speciesJoinDao.insertPokemonSpeciesJoin(charmanderSpeciesJoin)
            speciesJoinDao.insertPokemonSpeciesJoin(squirtleSpeciesJoin)

            pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
            pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)
            pokemonTypeJoinDao.insertPokemonTypeJoin(waterJoin)
            pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)

        }

//        val pokemonMatchingSearch =
//            pokemonDao.searchAndFilterPokemonWithTypesAndSpeciesOrderedByMatchesAndIds("%%", listOf())
//                .getValueBlocking(scope)

//        assertThat(pokemonMatchingSearch?.size, equalTo(3))
//
//        assertThat(pokemonMatchingSearch!![0].pokemon.name, equalTo(charmander.name))
//        assertThat(pokemonMatchingSearch!![1].pokemon.name, equalTo(squirtle.name))
//        assertThat(pokemonMatchingSearch!![2].pokemon.name, equalTo(bulbasaur.name))

        val pokemonMatchingSearchAndFilter =
            pokemonDao.searchAndFilterPokemonWithTypesAndSpeciesOrderedByMatchesAndIds("%%", arrayListOf("fire"))
                .getValueBlocking(scope)

        assertThat(pokemonMatchingSearchAndFilter?.size, equalTo(1))

        assertThat(
            pokemonMatchingSearchAndFilter!![0].pokemon.name,
            equalTo(charmander.name)
        )

    }

//    @Test
//    @Throws(Exception::class)
//    fun playingWithRawQuery() {
//        val bulbasaurSpeciesID = 1
//        val squirtleSpeciesID = 2
//        val charmanderSpeciesID = 3
//
//        val bulbasaurID = 3
//        val squirtleID = 2
//        val charmanderID = 1
//
//        val grassTypeID = 1
//        val poisonTypeID = 2
//        val fireTypeID = 3
//        val waterTypeID = 4
//
//        val charmander = Pokemon(id = charmanderID, name = "charmander")
//        val squirtle = Pokemon(id = squirtleID, name = "squirtle")
//        val bulbasaur = Pokemon(id = bulbasaurID, name = "bulbasaur")
//
//        val poisonType = PokemonType(id = poisonTypeID, name = "poison", slot = 1)
//        val grassType = PokemonType(id = grassTypeID, name = "grass", slot = 2)
//        val fireType = PokemonType(id = fireTypeID, name = "fire", slot = 1)
//        val waterType = PokemonType(id = waterTypeID, name = "water", slot = 1)
//
//        val speciesBulbasaur = PokemonSpecies(
//            id = bulbasaurSpeciesID,
//            species = "Seed pokemon",
//            pokedexEntry = "This pokemon is probably martins favourite after Raichu"
//        )
//        val speciesSquirtle = PokemonSpecies(
//            id = squirtleSpeciesID,
//            species = "Turtle pokemon",
//            pokedexEntry = "Small shell pokemon"
//        )
//        val speciesCharmander = PokemonSpecies(
//            id = charmanderSpeciesID,
//            species = "Fire lizard pokemon",
//            pokedexEntry = "If the flame on this pokemon's tail goes out it will die"
//        )
//
//        val bulbasaurSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = bulbasaurID, species_id = bulbasaurSpeciesID)
//        val charmanderSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = charmanderID, species_id = charmanderSpeciesID)
//        val squirtleSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = squirtleID, species_id = squirtleSpeciesID)
//
//        val poisonJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = poisonTypeID)
//        val grassJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = grassTypeID)
//        val waterJoin = PokemonTypesJoin(pokemon_id = squirtleID, type_id = waterTypeID)
//        val fireJoin = PokemonTypesJoin(pokemon_id = charmanderID, type_id = fireTypeID)
//
//        runBlocking {
//
//            pokemonDao.insertPokemon(bulbasaur)
//            pokemonDao.insertPokemon(squirtle)
//            pokemonDao.insertPokemon(charmander)
//
//            pokemonTypeDao.insertPokemonType(pokemonType = poisonType)
//            pokemonTypeDao.insertPokemonType(pokemonType = grassType)
//            pokemonTypeDao.insertPokemonType(pokemonType = fireType)
//            pokemonTypeDao.insertPokemonType(pokemonType = waterType)
//
//            speciesDao.insertSpecies(speciesBulbasaur)
//            speciesDao.insertSpecies(speciesCharmander)
//            speciesDao.insertSpecies(speciesSquirtle)
//
//            speciesJoinDao.insertPokemonSpeciesJoin(bulbasaurSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(charmanderSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(squirtleSpeciesJoin)
//
//            pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(waterJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)
//
//        }
//
//        val search = "%%"
//        val queryWithSearchString =
//            "SELECT * FROM Pokemon WHERE pokemon_name LIKE $search ORDER BY pokemon_id ASC"
//
//        val pokemonMatchingSearch =
//            pokemonDao.pokemonRawQuery(SimpleSQLiteQuery(queryWithSearchString))
//                .getValueBlocking(scope)
//
//        assertThat(pokemonMatchingSearch?.size, equalTo(3))
//
//        assertThat(pokemonMatchingSearch!![0].pokemon.name, equalTo(charmander.name))
//        assertThat(pokemonMatchingSearch!![1].pokemon.name, equalTo(squirtle.name))
//        assertThat(pokemonMatchingSearch!![2].pokemon.name, equalTo(bulbasaur.name))
//
//        val filter = "%fire%"
//        val queryWithSearchAndFilterString =
//            "SELECT * FROM Pokemon LEFT JOIN PokemonType ON pokemon_id = type_id AND type_name =$filter WHERE pokemon_name LIKE $search ORDER BY pokemon_name ASC"
//
//        val pokemonMatchingSearchAndFilter =
//            pokemonDao.pokemonRawQuery(SimpleSQLiteQuery(queryWithSearchAndFilterString))
//                .getValueBlocking(scope)
//
//        assertThat(pokemonMatchingSearchAndFilter?.size, equalTo(1))
//
//        assertThat(
//            pokemonMatchingSearchAndFilter!![0].pokemon.name,
//            equalTo(charmander.name)
//        )
//
//    }

//    @Test
//    @Throws(Exception::class)
//    fun searchAndFilterWithRawQuery() {
//
//        val search = "%%"
//
//        val bulbasaurSpeciesID = 1
//        val squirtleSpeciesID = 2
//        val charmanderSpeciesID = 3
//        val charizardSpeciesID = 4
//        val pidgeySpeciesID = 5
//        val moltresSpeciesID = 6
//
//        val bulbasaurID = 1
//        val squirtleID = 2
//        val charmanderID = 3
//        val charizardID = 4
//        val pidgeyID = 5
//        val moltresID = 6
//
//        val grassTypeID = 1
//        val poisonTypeID = 2
//        val fireTypeID = 3
//        val waterTypeID = 4
//        val flyingTypeID = 4
//
//        val bulbasaur = Pokemon(id = bulbasaurID, name = "bulbasaur")
//        val squirtle = Pokemon(id = squirtleID, name = "squirtle")
//        val charmander = Pokemon(id = charmanderID, name = "charmander")
//        val charizard = Pokemon(id = charizardID, name = "charizard")
//        val moltres = Pokemon(id = moltresID, name = "moltres")
//        val pidgey = Pokemon(id = pidgeyID, name = "pidgey")
//
//        val poisonType = PokemonType(id = poisonTypeID, name = "poison", slot = 1)
//        val grassType = PokemonType(id = grassTypeID, name = "grass", slot = 2)
//        val fireType = PokemonType(id = fireTypeID, name = "fire", slot = 1)
//        val waterType = PokemonType(id = waterTypeID, name = "water", slot = 1)
//        val flyingType = PokemonType(id = flyingTypeID, name = "flying", slot = 1)
//
//        val speciesBulbasaur = PokemonSpecies(
//            id = bulbasaurSpeciesID,
//            species = "Seed pokemon",
//            pokedexEntry = "This pokemon is probably martins favourite after Raichu"
//        )
//        val speciesSquirtle = PokemonSpecies(
//            id = squirtleSpeciesID,
//            species = "Turtle pokemon",
//            pokedexEntry = "Small shell pokemon"
//        )
//        val speciesCharmander = PokemonSpecies(
//            id = charmanderSpeciesID,
//            species = "Fire lizard pokemon",
//            pokedexEntry = "If the flame on this pokemon's tail goes out it will die"
//        )
//        val speciesCharizard = PokemonSpecies(
//            id = charizardSpeciesID,
//            species = "Fire flying lizard pokemon",
//            pokedexEntry = "Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally"
//        )
//        val speciesMoltres = PokemonSpecies(
//            id = moltresSpeciesID,
//            species = "Fire bird pokemon",
//            pokedexEntry = "Known as the legendary bird of fire. Every flap of its wings creates a dazzling flash of flames"
//        )
//        val speciesPidgey = PokemonSpecies(
//            id = pidgeySpeciesID,
//            species = "Bird pokemon",
//            pokedexEntry = "Pidgey is a Flying Pokémon. Among all the Flying Pokémon, it is the gentlest and easiest to capture. A perfect target for the beginning Pokémon Trainer to test his Pokémon's skills."
//        )
//
//        val bulbasaurSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = bulbasaurID, species_id = bulbasaurSpeciesID)
//        val charmanderSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = charmanderID, species_id = charmanderSpeciesID)
//        val squirtleSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = squirtleID, species_id = squirtleSpeciesID)
//        val charizardSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = charizardID, species_id = charizardSpeciesID)
//        val moltresSpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = moltresID, species_id = moltresSpeciesID)
//        val pidgeySpeciesJoin =
//            PokemonSpeciesJoin(pokemon_id = pidgeyID, species_id = pidgeySpeciesID)
//
//        val poisonJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = poisonTypeID)
//        val grassJoin = PokemonTypesJoin(pokemon_id = bulbasaurID, type_id = grassTypeID)
//        val waterJoin = PokemonTypesJoin(pokemon_id = squirtleID, type_id = waterTypeID)
//        val fireJoin = PokemonTypesJoin(pokemon_id = charmanderID, type_id = fireTypeID)
//        val fireJoin2 = PokemonTypesJoin(pokemon_id = charizardID, type_id = fireTypeID)
//        val fireJoin3 = PokemonTypesJoin(pokemon_id = moltresID, type_id = fireTypeID)
//        val flyingJoin = PokemonTypesJoin(pokemon_id = charizardID, type_id = flyingTypeID)
//        val flyingJoin2 = PokemonTypesJoin(pokemon_id = moltresID, type_id = flyingTypeID)
//        val flyingJoin3 = PokemonTypesJoin(pokemon_id = pidgeyID, type_id = flyingTypeID)
//
//        runBlocking {
//
//            pokemonDao.insertPokemon(bulbasaur)
//            pokemonDao.insertPokemon(squirtle)
//            pokemonDao.insertPokemon(charmander)
//            pokemonDao.insertPokemon(charizard)
//            pokemonDao.insertPokemon(moltres)
//            pokemonDao.insertPokemon(pidgey)
//
//            pokemonTypeDao.insertPokemonType(pokemonType = poisonType)
//            pokemonTypeDao.insertPokemonType(pokemonType = grassType)
//            pokemonTypeDao.insertPokemonType(pokemonType = fireType)
//            pokemonTypeDao.insertPokemonType(pokemonType = waterType)
//            pokemonTypeDao.insertPokemonType(pokemonType = flyingType)
//
//            speciesDao.insertSpecies(speciesBulbasaur)
//            speciesDao.insertSpecies(speciesCharmander)
//            speciesDao.insertSpecies(speciesSquirtle)
//            speciesDao.insertSpecies(speciesCharizard)
//            speciesDao.insertSpecies(speciesMoltres)
//            speciesDao.insertSpecies(speciesPidgey)
//
//            speciesJoinDao.insertPokemonSpeciesJoin(bulbasaurSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(charmanderSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(squirtleSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(charizardSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(moltresSpeciesJoin)
//            speciesJoinDao.insertPokemonSpeciesJoin(pidgeySpeciesJoin)
//
//            pokemonTypeJoinDao.insertPokemonTypeJoin(poisonJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(grassJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(waterJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin2)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(fireJoin3)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin2)
//            pokemonTypeJoinDao.insertPokemonTypeJoin(flyingJoin3)
//
//            val queryWithSearchString =
//                "SELECT * FROM Pokemon WHERE pokemon_name LIKE $search ORDER BY pokemon_id ASC"
//
//            val pokemonMatchingSearch =
//                pokemonDao.pokemonRawQuery(SimpleSQLiteQuery(queryWithSearchString))
//                    .getValueBlocking(scope)
//
//            assertThat(pokemonMatchingSearch?.size, equalTo(6))
//
//            println("POKEMON_NAME" + pokemonMatchingSearch!![0].pokemon.name)
//            println("POKEMON_NAME" + pokemonMatchingSearch!![1].pokemon.name)
//            println("POKEMON_NAME" + pokemonMatchingSearch!![2].pokemon.name)
//
//            assertThat(pokemonMatchingSearch!![0].pokemon.name, equalTo(bulbasaur.name))
//            assertThat(pokemonMatchingSearch!![1].pokemon.name, equalTo(charmander.name))
//            assertThat(pokemonMatchingSearch!![2].pokemon.name, equalTo(squirtle.name))
//
//        }
//
//
//        /*
//        But you also have to pull the type relation table into the query before you can conduct that filter properly.
//        Something like
//        select * from pokemon left join pokemonTypes on pokemon.id = pokemonTypes.id AND pokemonTypes.type = :filter1
//        AND pokemonTypes.type =: filter2 AND ... as many "ands" as the user selects and you generate these ands in
//        your rawquery. The result will be a list of each pokemon that makes at least one of those filters or more
//        and if you want to group them and sort them you do that after all the "ands"
//         */
//
////                val queryWithSearchAndFilterList = "SELECT * FROM Pokemon LEFT JOIN PokemonType ON pokemon_id = type_id WHERE pokemon_name LIKE :search AND type_name IN ($filters) ORDER BY pokemon_name ASC"
////                val pokemonMatchingSearchAndFilter =
////                    pokemonDao.pokemonRawQuery(SimpleSQLiteQuery(queryWithSearchAndFilterList)).getValueBlocking(scope)
//
//
//
//
//
//
//
//
//        val filters = arrayListOf("fire", "flying")
//
//        val pokemonMatchingSearchAndFilter = pokemonDao
//            .searchAndFilterPokemonWithTypesAndSpeciesOrderedByMatchesAndIds(
//                search,
//                filters
//            ).getValueBlocking(scope)
//
//        assertThat(pokemonMatchingSearchAndFilter?.size, equalTo(3))
//
//        println("POKEMON_NAME" + pokemonMatchingSearchAndFilter!![0].pokemon.name)
//        println("POKEMON_NAME" + pokemonMatchingSearchAndFilter!![1].pokemon.name)
//        println("POKEMON_NAME" + pokemonMatchingSearchAndFilter!![2].pokemon.name)
//
//        assertThat(
//            pokemonMatchingSearchAndFilter!![0].pokemon.name,
//            equalTo(charmander.name)
//        )
//        assertThat(
//            pokemonMatchingSearchAndFilter!![1].pokemon.name,
//            equalTo(charizard.name)
//        )
//        assertThat(pokemonMatchingSearchAndFilter!![2].pokemon.name, equalTo(moltres.name))
//
//    }

}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.getValueBlocking(scope: CoroutineScope): T? {
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