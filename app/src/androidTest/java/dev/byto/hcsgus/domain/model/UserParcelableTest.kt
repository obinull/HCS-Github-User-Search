package dev.byto.hcsgus.domain.model

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserParcelableTest {

    @Test
    fun `user is parcelable and can be restored`() {
        val originalUser = User(
            id = 456,
            login = "parcelUser",
            avatarUrl = "https://example.com/parcel_avatar.png"
        )

        // Write to parcel
        val parcel = Parcel.obtain()
        originalUser.writeToParcel(parcel, originalUser.describeContents())

        // Reset parcel for reading
        parcel.setDataPosition(0)

        // Read from parcel
        val restoredUser = User.CREATOR.createFromParcel(parcel)

        // Assertions
        assertEquals(originalUser.id, restoredUser.id)
        assertEquals(originalUser.login, restoredUser.login)
        assertEquals(originalUser.avatarUrl, restoredUser.avatarUrl)
        assertEquals(originalUser, restoredUser) // Relies on User being a data class

        // Recycle the parcel
        parcel.recycle()
    }
}
