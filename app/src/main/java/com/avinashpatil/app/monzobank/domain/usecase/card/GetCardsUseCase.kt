package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    operator fun invoke(userId: String): Flow<List<Card>> {
        return cardRepository.getCardsByUserId(userId)
    }
    
    suspend fun getCardsByAccount(accountId: String): List<Card> {
        return cardRepository.getCardsByAccountId(accountId)
    }
    
    suspend fun getCardsByType(userId: String, cardType: CardType): List<Card> {
        return cardRepository.getCardsByType(userId, cardType)
    }
    
    suspend fun getCardById(cardId: String): Card? {
        return cardRepository.getCardById(cardId)
    }
}