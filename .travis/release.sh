#!/usr/bin/env bash

set -o errexit -o nounset

if [ "$TRAVIS_BRANCH" != "master" ]; then
  echo "This commit was made against the $TRAVIS_BRANCH and not the master! No release!"
  exit 0
fi

git config user.name "Dwolla Bot"
git config user.email "dev+dwolla-bot@dwolla.com"

sbt "release with-defaults"
