package dev.byto.hcsgus.domain.usecase

import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): Flow<Result<UserDetail>> {
        return userRepository.getUserDetail(username)
    }
}