package solid.inject.debug

import solid.inject.core.Provider
import solid.inject.core.ProviderRegistry

internal class DebugContextProvider(
  private val typeId: String,
  private val provider: Provider) : Provider
{
  override fun invoke(registry: ProviderRegistry): Any?
  {
    val context = ContextualRegistry.from(registry)
    val instance = provider.invoke(context)
    context.compute(typeId, instanceId(instance!!))

    return instance
  }

  private fun instanceId(instance: Any): String
  {
    return instance
      .toString()
      .substringAfterLast('@')
  }
}
