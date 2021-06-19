package solid.inject

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class InjectorForking
{
  @Test
  fun `forked injectors give correct instances`()
  {
    val instance1 = C()
    val instance2 = C()
    val facade1 = Injection()
    val facade2 = facade1.fork()
    facade1.register<C> { instance1 }
    facade2.register<C> { instance2 }

    assertThat(facade1.gimme<C>()).isSameAs(instance1)
    assertThat(facade2.gimme<C>()).isSameAs(instance2)
    assertThat(instance1).isNotSameAs(instance2)
  }

  @Test
  fun `child (forked) injector inherits parent registrations`()
  {
    val facade1 = Injection()
    facade1.register(InjectorForking::C)
    val facade2 = facade1.fork()

    assertThat(facade2.gimme<C>()).isNotNull
  }

  @Test
  fun `child (forked) injector is not mutated by parent registrations`()
  {
    val facade1 = Injection()
    val facade2 = facade1.fork()
    facade1.register(InjectorForking::C)

    assertThatExceptionOfType(NullPointerException::class.java)
      .isThrownBy { facade2.gimme<C>() }
  }

  @Test
  fun `parent injector is not mutated by child (forked) registrations`()
  {
    val facade1 = Injection()
    val facade2 = facade1.fork()
    facade2.register(InjectorForking::C)

    assertThatExceptionOfType(NullPointerException::class.java)
      .isThrownBy { facade1.gimme<C>() }
  }

  class C
}
