<h2>PokeApp - A reference guide for great android development</h2>
<p>There are some questionable architecture choices largely due to the pokeApi being free
and limited in aspects such as not being able to get paged queries or filtered results</p>

To add a new table
1. Add an object to app/java/com/sealstudios/pokemonApp/database/object
2. Add a dao for your object to app/java/com/sealstudios/pokemonApp/database/dao
3. Add your dao and object to  app/java/com/sealstudios/pokemonApp/database/PokemonRoomDatabase
5. Create a provider for your dao in  app/java/com/sealstudios/pokemonApp/di/database


