package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.auth.AuthDataSource
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.TokenEnvelope
import com.example.irumi.data.mapper.toBaseEntity
import com.example.irumi.domain.entity.BaseEntity
import com.example.irumi.domain.entity.MemberProfileEntity
import com.example.irumi.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val ds: AuthDataSource
) : AuthRepository {

    override suspend fun signUp(req: SignUpRequest): Result<BaseEntity<LoginEnvelope>> =
        runCatching { ds.signUp(req).toBaseEntity() }

    override suspend fun login(req: LoginRequest): Result<LoginEnvelope> =
        runCatching { ds.login(req).data!! }

    override suspend fun logout(): Result<Unit> =
        runCatching { ds.logout(); Unit }

    override suspend fun reissue(): Result<TokenEnvelope> =
        runCatching { ds.reissue().data!! }

    override suspend fun updateMe(req: AuthEditRequest): Result<MemberProfileEntity> =
        runCatching {
            val r = ds.updateMe(req).data!!
            MemberProfileEntity(
                profileImage = r.profileImage,
                name = r.name,
                email = r.email,
                password = r.password,
                budget = r.budget
            )
        }

    override suspend fun postAiTransaction(): Result<Unit> =
        runCatching { ds.postAiTransaction() }

    override suspend fun postMission(): Result<Unit> =
        runCatching { ds.postMission() }
}
