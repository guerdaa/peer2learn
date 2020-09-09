package com.pse.peer2learn.repositories

import com.google.firebase.firestore.FirebaseFirestore

/**
 * An Abstract repository that defines the methods for the concrete Repositories.
 */
abstract class AbstractRepository<T> {

    protected val firebaseInstance = FirebaseFirestore.getInstance()

    /**
     * Creates an [instance] in a specific [T] collection
     */
    abstract fun create(instance: T)

    /**
     * Deletes an [instance] in a specific [T] collection
     */
    abstract fun delete(instance: T)

    /**
     * Updates an [instance] in a specific [T] collection
     */
    abstract fun update(instance: T)
}