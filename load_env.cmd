@echo off
:: Vérifier si un fichier est passé en argument
if "%~1"=="" (
    echo Usage: load_env.cmd chemin\vers\.env
    exit /b 1
)

:: Vérifier si le fichier existe
if not exist "%~1" (
    echo Erreur : Le fichier %~1 n'existe pas.
    exit /b 1
)

echo Chargement des variables d'environnement depuis %~1...

:: Lire le fichier ligne par ligne
for /f "tokens=* delims=" %%i in (%~1) do (
    set "line=%%i"

    :: Ignorer les lignes vides ou les commentaires (#)
    if not "!line!"=="" (
        if not "!line:~0,1!"=="#" (
            :: Extraire la clé et la valeur
            for /f "tokens=1,2 delims==" %%a in ("!line!") do (
                set "key=%%a"
                set "value=%%b"

                :: Exporter la variable d'environnement
                setx !key! "!value!" >nul
                echo Exporté : !key! = !value!
            )
        )
    )
)

echo Toutes les variables d'environnement ont été exportées avec succès.
