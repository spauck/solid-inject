package solid.inject

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Scoping
{
  /**
   * This test is meant to catch a bug where eagerly
   * creating scoped instances creates a scoped instance
   * though a dependency before the scoped provider override
   * is in place.
   *
   * This is dependent on instantiation order, which is
   * dependent on the map implementation backing the
   * scoping information!
   *
   * Since B depends on C and both are scoped, instantiating
   * B before C could instantiate C before the 'singleton'
   * provider for C is set up.
   */
  @Test
  fun `Scoping works with order dependent instantiation`()
  {
    val inject = Injection()
    inject.register(Scoping::A)
    inject.register(Scoping::B)
    inject.register(Scoping::C)
    inject.scope<A, B>()
    inject.scope<A, C>()

    val a = inject.gimme<A>()

    assertThat(a.c).isSameAs(a.b.c)
  }

  /**
   * This test is meant to catch a class of bug that comes from
   * not using the injection context passed to the scoped provider.
   *
   * Doing so means inner scopes (B here) will bypass the outer
   * injection scoped created before (A here) and get a separate
   * instance for something that should be shared (C here).
   *
   * This was initially caused when the scoped provider was
   * declared as an inner class and referenced the outer class
   * gimme method.
   */
  @Test
  fun `An inner scope should not disrupt an outer scope`()
  {
    val inject = Injection()
    inject.register(Scoping::A)
    inject.register(Scoping::B)
    inject.register(Scoping::C)
    inject.scope<A, C>()
    inject.scope<B, C>()
    inject.scope<A, B>()

    val a = inject.gimme<A>()

    assertThat(a.c).isSameAs(a.b.c)
  }

  class A(val c: C, val b: B)

  class B(val c: C)

  class C
}
