package com.TMDAD_2024.message

import com.TMDAD_2024.user.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MessageRepository : CrudRepository<Message, Int>
{
    //Para obtener los mensajes que pertenecen a una room dado el Id en BBDD de la room
    fun findByRoomId(roomId: Int) : List<Message>
}