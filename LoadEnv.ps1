# Vérifier si un fichier .env est fourni en argument
param(
    [string]$EnvFile = ""
)

if (-not $EnvFile) {
    Write-Host "Usage: .\LoadEnv.ps1 -EnvFile <chemin/vers/.env>"
    return
}

# Vérifier si le fichier existe
if (-not (Test-Path $EnvFile)) {
    Write-Host "Erreur : Le fichier $EnvFile n'existe pas."
    return
}

# Charger et exporter les variables d'environnement
Write-Host "Chargement des variables d'environnement depuis $EnvFile"

Get-Content $EnvFile | ForEach-Object {
    # Ignorer les lignes vides ou les commentaires
    if (-not ($_ -match "^\s*#") -and ($_ -match "=")) {
        $parts = $_ -split "=", 2
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()

        # Exporter la variable d'environnement
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "Exporté : $key=$value"
    }
}

Write-Host "Toutes les variables d'environnement ont été exportées avec succès."
