function New-MySqlServer {
    param (
        [string]$Name = "mysql-server",
        [string]$Password = "toor"
    )

    docker run `
        --name $Name `
        --publish "3306:3306" `
        --env "MYSQL_ROOT_PASSWORD=$Password" `
        --detach "mysql:latest"
}

function New-PostgresqlServer {
    param (
        [string]$Name = "postgresql-server",
        [string]$Password = "toor"
    )

    docker run `
        --name $Name `
        --publish "5432:5432" `
        --env "POSTGRES_PASSWORD=$Password" `
        --detach "postgres:latest"
}

function New-MongoServer {
    param (
        [string]$Name = "mongo-server"
    )

    docker run `
        --name $Name `
        --publish "27017:27017" `
        --detach "mongo:latest"
}

function New-RedisServer {
    param (
        [string]$Name = "redis-server"
    )

    docker run `
        --name $Name `
        --publish "6379:6379" `
        --detach "redis:latest"
}

New-PostgresqlServer `
            -Name "postgresql-server" `
            -Password "toor"
