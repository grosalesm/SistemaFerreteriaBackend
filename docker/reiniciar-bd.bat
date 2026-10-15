@echo off
REM Borra TODOS los datos y deja las 6 bases vacias.
REM Al levantar los microservicios, Hibernate crea las tablas y los DataInitializer cargan los datos de demo.
REM Antes de ejecutarlo: detener todos los microservicios (Docker debe seguir corriendo).

echo Este script borra todos los datos de la ferreteria.
set /p CONFIRMA=Escriba SI para continuar:
if /I not "%CONFIRMA%"=="SI" goto fin

for %%B in (db_auth db_clientes db_productos db_cotizaciones db_ventas db_notificaciones) do (
    echo Reiniciando %%B ...
    docker exec ferreteria-postgres psql -U ferreteria -d postgres -c "DROP DATABASE IF EXISTS %%B WITH (FORCE);"
    docker exec ferreteria-postgres psql -U ferreteria -d postgres -c "CREATE DATABASE %%B;"
)

echo Vaciando colas de RabbitMQ ...
for %%Q in (productos.queue cotizaciones.queue ventas.queue notificaciones.queue) do (
    docker exec ferreteria-rabbitmq rabbitmqctl purge_queue %%Q
)

echo.
echo Listo. Ahora levante Eureka, luego ms-clientes, ms-auth, ms-productos y el resto.
:fin
pause
