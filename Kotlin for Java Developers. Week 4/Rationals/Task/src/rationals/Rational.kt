package rationals

import java.math.BigInteger

class Rational(numerator: BigInteger, denominator: BigInteger)
    : Comparable<Rational> {

    private val numerator: BigInteger
    private val denominator: BigInteger

    constructor(numerator: Int) : this(numerator.toBigInteger(), BigInteger.ONE)
    constructor(numerator:Long) : this(numerator.toBigInteger(), BigInteger.ONE)
    constructor(numerator: BigInteger) : this(numerator, BigInteger.ONE)

    init {
        if (denominator == BigInteger.ZERO) {
            throw IllegalArgumentException()
        }
        val (num, den) = normal(numerator, denominator)
        this.numerator = num
        this.denominator = den
        //println(this)
    }

    private fun normal(num: BigInteger, den: BigInteger): Pair<BigInteger, BigInteger> {
        val gcd = num.gcd(den)
        return if (den < BigInteger.ZERO) -num/gcd to -den/gcd else num/gcd to den/gcd
    }

    override fun toString(): String {
        return if (denominator == BigInteger.ONE) "$numerator" else "$numerator/$denominator"
    }

    override fun compareTo(other: Rational): Int {
        //val (l, r) = this.numerator*other.denominator to other.numerator*this.denominator
        val (left, right) = this.numerator*other.denominator to other.numerator*this.denominator
        return left.compareTo(right)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Rational) return false
        return numerator== other.numerator && denominator == other.denominator
    }

    operator fun unaryPlus(): Rational = Rational(numerator, denominator)
    operator fun unaryMinus(): Rational = Rational(-numerator, denominator)

    operator fun plus(other: Rational): Rational {
        val (num, den) = normal(this.numerator*other.denominator + other.numerator*this.denominator, this.denominator*other.denominator)
        return num divBy den
    }

    operator fun minus(other: Rational): Rational {
        val (num, den) = normal(this.numerator*other.denominator - other.numerator*this.denominator, this.denominator*other.denominator)
        return num divBy den
    }

    operator fun times(other: Rational): Rational {
        val (num, den) = normal(this.numerator * other.numerator,
                this.denominator * other.denominator)
        return num divBy den
    }

    operator fun div(other: Rational): Rational
    {
        val (num, den) = normal(this.numerator * other.denominator,
            this.denominator * other.numerator)
        return num divBy den
    }
}

fun String.toRational(): Rational {
    val (num, den) = if('/' in this) run {
        val args = this.split('/')
        args[0].toBigInteger() to args[1].toBigInteger()
    }
    else this.toBigInteger() to BigInteger.ONE
    return Rational(num, den)
}

infix fun Int.divBy(number: Int): Rational
        = Rational(this.toBigInteger(), number.toBigInteger())
infix fun Long.divBy(number: Long): Rational
        = Rational(this.toBigInteger(), number.toBigInteger())
infix fun BigInteger.divBy(number: BigInteger): Rational
        = Rational(this, number)

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}