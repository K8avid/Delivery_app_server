#!/bin/bash

# Vérifier si un fichier .env est passé en paramètre
if [ -z "$1" ]; then
  echo "Usage: source load_env.sh /chemin/vers/.env"
  return 1
fi

# Vérifier si le fichier existe
if [ ! -f "$1" ]; then
  echo "Erreur : le fichier $1 n'existe pas."
  return 1
fi

# Charger le fichier .env
echo "Chargement des variables d'environnement depuis $1"
while IFS='=' read -r key value; do
  # Ignorer les lignes vides ou les commentaires
  if [[ ! "$key" =~ ^# && -n "$key" ]]; then
    export "$key"="$value"
    echo "Exporté : $key"
  fi
done < "$1"

echo "Toutes les variables d'environnement ont été exportées avec succès."
