package solid.inject.core

internal class BindingProvider(
  private val boundId: String,
) : Provider
{
  override fun invoke(context: ProviderRegistry) =
    context.gimme(boundId)!!.invoke(context)
}
