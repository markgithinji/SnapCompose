import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composegallery.feature.gallery.data.local.AppDatabase
import com.example.composegallery.feature.gallery.data.local.RecentSearchDao
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentSearchDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: RecentSearchDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.recentSearchDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun returns_most_recent_searches_first() = runTest {
        val search1 = RecentSearch(query = "Water", timestamp = System.currentTimeMillis())
        val search2 =
            RecentSearch(query = "Winter Garden", timestamp = System.currentTimeMillis() + 1)

        dao.insertSearch(search1)
        dao.insertSearch(search2)

        val results = dao.getRecentSearches(limit = 10).first()

        assertThat(results).hasSize(2)
        assertThat(results[0].query).isEqualTo("Winter Garden")
        assertThat(results[1].query).isEqualTo("Water")
    }

    @Test
    fun removes_specific_search_entry() = runTest {
        val search = RecentSearch(query = "Winter", timestamp = System.currentTimeMillis())
        dao.insertSearch(search)

        dao.deleteSearch("Winter")

        val results = dao.getRecentSearches().first()
        assertThat(results).isEmpty()
    }

    @Test
    fun deleteSearchIgnoreCase_deletes_case_insensitively() = runTest {
        val search = RecentSearch("Winter Garden", timestamp = 1000L)
        dao.insertSearch(search)

        dao.deleteSearchIgnoreCase("WiNtEr GaRdEn")

        val results = dao.getRecentSearches().first()
        assertThat(results).isEmpty()
    }

    @Test
    fun deleteSearch_doesNotDelete_whenCaseDoesNotMatch() = runTest {
        val search = RecentSearch("Nature", timestamp = 1000L)
        dao.insertSearch(search)

        dao.deleteSearch("nature") // lowercase mismatch

        val results = dao.getRecentSearches().first()
        assertThat(results).hasSize(1)
    }

    @Test
    fun replaces_existing_query_when_inserted_again() = runTest {
        val search = RecentSearch("Duplicate", timestamp = 1000L)
        dao.insertSearch(search)
        dao.insertSearch(search.copy(timestamp = 2000L))

        val results = dao.getRecentSearches().first()
        assertThat(results).hasSize(1)
        assertThat(results[0].timestamp).isEqualTo(2000L)
    }

    @Test
    fun getRecentSearches_respects_limit() = runTest {
        val now = 1000L
        repeat(5) {
            dao.insertSearch(RecentSearch("Search $it", now + it))
        }

        val results = dao.getRecentSearches(limit = 3).first()

        assertThat(results).hasSize(3)
        assertThat(results[0].query).isEqualTo("Search 4") // Most recent
    }

    @Test
    fun clears_all_search_entries() = runTest {
        dao.insertSearch(RecentSearch("Water", System.currentTimeMillis()))
        dao.insertSearch(RecentSearch("Winter Garden", System.currentTimeMillis()))

        dao.clearSearches()

        val results = dao.getRecentSearches().first()
        assertThat(results).isEmpty()
    }
}
