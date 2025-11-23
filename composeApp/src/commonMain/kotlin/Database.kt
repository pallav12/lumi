package com.desktop.lumi.db
class Database(driverFactory: DatabaseDriverFactory) {
    val db = AppDatabase(driverFactory.createDriver())

    val personQueries = db.personQueries
}
