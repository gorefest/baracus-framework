package net.mantucon.baracus.annotations;

/**
 *
 * Bean annotation denoting a bean as a component or a variable as an injection target.<br><hr>
 *
 *
 * Because baracus relies on type based injection
 * this annotation has only a documentary character
 * in order to enable you to mark variables a bean injection
 * targets. If you leave it out, it has no effect on the injection
 * process which is relying on types.<br>
 *
 * Use this bean to document components and injection targets as well.
 *
 */
public @interface Bean {
}
