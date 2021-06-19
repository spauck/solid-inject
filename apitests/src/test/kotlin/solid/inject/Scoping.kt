package solid.inject

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Scoping
{
  @Test
  fun `Scoping results in the same instance injected throughout a scope`()
  {
    val inject = Injection()
    inject.register(Scoping::D)
    inject.register(Scoping::C)
    // Assert pre-condition
    val unscoped = inject.gimme<D>()
    assertThat(unscoped.c1).isNotSameAs(unscoped.c2)

    inject.scope<D, C>()
    val scoped = inject.gimme<D>()

    assertThat(scoped.c1).isSameAs(scoped.c2)
  }

  @Test
  fun `Scoping on a concrete type applies behind an interface`()
  {
    val inject = Injection()
    inject.bind<E, D>()
    inject.register(Scoping::D)
    inject.register(Scoping::C)

    inject.scope<D, C>()
    val e = inject.gimme<E>()

    assertThat(e.c1).isSameAs(e.c2)
  }

  @Test
  fun `Scoping on an interface applies through that interface`()
  {
    val inject = Injection()
    inject.bind<E, D>()
    inject.register(Scoping::D)
    inject.register(Scoping::C)

    inject.scope<E, C>()
    val e = inject.gimme<E>()

    assertThat(e.c1).isSameAs(e.c2)
  }

  @Test
  fun `Scoping on an interface does not apply to a concrete type not injected through the interface`()
  {
    val inject = Injection()
    inject.bind<E, D>()
    inject.register(Scoping::D)
    inject.register(Scoping::C)

    inject.scope<E, C>()
    val d = inject.gimme<D>()

    assertThat(d.c1).isNotSameAs(d.c2)
  }

  class D(
    override val c1: C,
    override val c2: C,
  ) : E

  interface E
  {
    val c1: C
    val c2: C
  }

  @Test
  fun `Scoped instances aren't shared between scopes`()
  {
    val inject = Injection()
    inject.register(Scoping::B)
    inject.register(Scoping::C)
    inject.scope<B, C>()

    val b1 = inject.gimme<B>()
    val b2 = inject.gimme<B>()

    assertThat(b1.c).isNotSameAs(b2.c)
  }

  @Test
  fun `A scoped instance is the same throughout object graph`()
  {
    val inject = Injection()
    inject.register(Scoping::A)
    inject.register(Scoping::B)
    inject.register(Scoping::C)
    inject.scope<A, C>()

    val a = inject.gimme<A>()

    assertThat(a.c).isSameAs(a.b.c)
  }

  class A(val c: C, val b: B)

  class B(val c: C)

  class C
}
