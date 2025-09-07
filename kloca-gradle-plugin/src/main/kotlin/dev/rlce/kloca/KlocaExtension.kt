package dev.rlce.kloca

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class KlocaExtension @Inject constructor(objects: ObjectFactory) {
    val defaultLanguage: Property<String> = objects.property(String::class.java)
        .convention("en")

    val namespacePrefix: Property<String> = objects.property(String::class.java)
        .convention("")
}
