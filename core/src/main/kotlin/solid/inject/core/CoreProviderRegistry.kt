package solid.inject.core

class CoreProviderRegistry(
  private val registrations: MutableMap<String, Provider> = LinkedHashMap(),
  private val scopes: MutableMap<String, MutableSet<String>> = LinkedHashMap()) : ProviderRegistry
{

  override fun bind(
    abstractionId: String,
    implementerId: String)
  {
    registrations[abstractionId] = BindingProvider(implementerId)
  }

  override fun register(
    id: String,
    provider: Provider)
  {
    registrations[id] = provider
  }

  override fun scope(
    toThat: String,
    scopeThis: String)
  {
    scopesFor(toThat).add(scopeThis)
  }

  private fun scopesFor(
    id: String): MutableSet<String>
  {
    return scopes.getOrPut(id, ::mutableSetOf)
  }

  override fun gimme(id: String): Provider?
  {
    val provider = registrations[id]

    // Potential for optional call here
    return ScopedProvider.from(scopesFor(id), provider)
  }

  override fun fork(): ProviderRegistry
  {
    return CoreProviderRegistry(
      LinkedHashMap(registrations),
      LinkedHashMap(scopes))
  }
}
