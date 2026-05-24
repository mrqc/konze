package net.masterstudios.konze.spring

object DataSourceContextHolder {
    private val CONTEXT = ThreadLocal<String>()

    @JvmStatic
    fun setDataSourceType(dataSourceType: String?) {
        CONTEXT.set(dataSourceType)
    }

    @JvmStatic
    fun getDataSourceType(): String? {
        return CONTEXT.get()
    }

    @JvmStatic
    fun clearDataSourceType() {
        CONTEXT.remove()
    }
}
