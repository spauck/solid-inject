package solid.inject

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test

class BasicUsage
{
  @Test
  fun `get an instance of a concrete class`()
  {
    val inject = Injection()
    inject.register(::Concrete)

    val instance = inject.gimme<Concrete>()

    assertThat(instance).isNotNull
  }

  @Test
  fun `every call to gimme can construct a new instance of a concrete class`()
  {
    val inject = Injection()
    inject.register(::Concrete)

    val instance1 = inject.gimme<Concrete>()
    val instance2 = inject.gimme<Concrete>()

    assertThat(instance1).isNotSameAs(instance2)
  }

  @Test
  fun `use a concrete class as an implementation for an abstract type`()
  {
    val inject = Injection()
    inject.bind<Abstract, Concrete>()
    inject.register(::Concrete)

    val instance = inject.gimme<Abstract>()

    assertThat(instance).isInstanceOf(Concrete::class.java)
  }

  @Test
  fun `can create an instance that needs parameters`()
  {
    val inject = Injection()
    inject.bind<Abstract, Concrete>()
    inject.register(::Nested)
    inject.register(::Concrete)

    val instance = inject.gimme<Nested>()

    assertThat(instance).isNotNull
    assertThat(instance.anAbstract).isNotNull
  }

  @Test
  fun `can inject directly into a function without registration`()
  {
    val inject = Injection()
    inject.bind<Abstract, Concrete>()
    inject.register(::Concrete)

    val instance = inject.gimmeNow(::Nested)

    assertThat(instance).isNotNull
    assertThat(instance.anAbstract).isNotNull
  }

  @Test
  fun `can use the injector inside a provider function`()
  {
    val inject = Injection()
    inject.bind<Abstract, Concrete>()
    inject.register(::Concrete)
    inject.provider { Nested(gimme()) }

    val instance = inject.gimme<Nested>()

    assertThat(instance).isNotNull
    assertThat(instance.anAbstract).isNotNull
  }

  @Test
  fun `Specified generics must match exactly to resolve`()
  {
    val inject = Injection()

    inject.register<TestGenerics<List<String>>>(::TestGenerics)
    val instance = inject.gimme<TestGenerics<List<String>>>()

    assertThat(instance).isNotNull
  }

  @Test
  fun `Specified generics that do not match do not resolve`()
  {
    val inject = Injection()

    inject.register<TestGenerics<Any>>(::TestGenerics)

    assertThatExceptionOfType(NullPointerException::class.java)
      .isThrownBy { inject.gimme<TestGenerics<List<String>>>() }
  }

  @Test
  fun `Unspecified generics are not supported`()
  {
    assertThatIllegalArgumentException()
      .isThrownBy { registerUnspecifiedGeneric<String>(Injection()) }
  }

  private inline fun <reified T> registerUnspecifiedGeneric(inject: Injection)
  {
    inject.register<TestGenerics<List<T>>>(::TestGenerics)
  }

  class Nested(val anAbstract: Abstract)

  class Concrete : Abstract

  interface Abstract

  class TestGenerics<T>
}