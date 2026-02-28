package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.avinashpatil.app.monzobank.data.remote.dto.TransactionDto
import com.avinashpatil.app.monzobank.data.remote.dto.common.PaginationDto
import com.google.gson.annotations.SerializedName

data class TransactionListDto(
    @SerializedName("transactions")
    val transactions: List<TransactionDto>,
    @SerializedName("pagination")
    val pagination: PaginationDto,
    @SerializedName("total_count")
    val totalCount: Int
)