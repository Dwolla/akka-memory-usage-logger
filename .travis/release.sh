#!/usr/bin/env bash

set -o errexit -o nounset

if [ "$TRAVIS_BRANCH" != "master" ]; then
  echo "This commit was made against the $TRAVIS_BRANCH and not the master! No release!"
  exit 0
fi

git config user.name "Dwolla Bot"
git config user.email "dev+dwolla-bot@dwolla.com"

git remote add release git@github.com:Dwolla/akka-memory-usage-logger.git
git fetch release

git clean -dxf
git checkout master
git branch --set-upstream-to=release/master

MASTER=$(git rev-parse HEAD)
if [ "$TRAVIS_COMMIT" != "$MASTER" ]; then
  echo "Checking out master set HEAD to $MASTER, but Travis was building $TRAVIS_COMMIT, so refusing to continue."
  exit 0
fi

sbt clean "release with-defaults"
