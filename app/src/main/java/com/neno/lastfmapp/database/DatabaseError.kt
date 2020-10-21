package com.neno.lastfmapp.database

sealed class DatabaseError(message: String) : Throwable(message)
{
    object NullResult : DatabaseError("An error occurred while loading from the database.")
}