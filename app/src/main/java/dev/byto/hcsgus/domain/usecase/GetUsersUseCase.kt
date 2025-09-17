package dev.byto.hcsgus.domain.usecase

import androidx.paging.PagingData
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<PagingData<User>> {
        return userRepository.getUsers()
    }
}