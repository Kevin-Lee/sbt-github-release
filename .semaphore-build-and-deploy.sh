#!/bin/bash

sbt clean
sbt writeVersion
sbt test
sbt package
export THIS_BRANCH="$BRANCH_NAME"
if [ "$THIS_BRANCH" == "fix-conflict-with-sbt-play-plugin-personal-deploy" ];
  then

  export PROJECT_VERSION=`cat target/version.sbt`

  echo "PROJECT_VERSION=$PROJECT_VERSION"

  if [ ! -n "$PROJECT_VERSION" ]
    then
    echo "NO PROJECT_VERSION is found so quit!"
    exit 1
  fi

  export GIT_TAG="v$PROJECT_VERSION"
  echo "GIT_TAG=$GIT_TAG"
  export PROJECT_BUILD_NAME="$GIT_TAG"
  echo "PROJECT_BUILD_NAME=$PROJECT_BUILD_NAME"

  echo "check git ls-remote --exit-code --tags origin $GIT_TAG 2>&1 > /dev/null"

  if git ls-remote --exit-code --tags origin $GIT_TAG 2>&1 > /dev/null ; then
    echo "the given tag '$GIT_TAG' already exists so skip it!"
  else
    echo "the given tag '$GIT_TAG' does not exist so run it!"
    git config --global user.email "builder+github@lckymn.com"
    git config --global user.name "Kevin-App-Builder"

    git tag "$GIT_TAG" -a -m "Automatically generated tag by Semaphore CI for $GIT_TAG"
    git push git@github.com:Kevin-Lee/sbt-github-release --tags

  fi

  echo "============================================"
  echo "Deploying to Bintray"
  if sbt publish ; then
    echo "Uploading to Bintray: Done"
    echo "============================================"
    echo "Deploying to GitHub"

    echo "======================================================"
    echo "ls -l target/scala-*/*.jar"
    ls -l target/scala-*/*.jar
    echo "======================================================"
    if [ -d "target/ci" ]; then
      echo "Clean up existing target/ci/*"
      echo "rm -R target/ci/*"
      rm -R target/ci/*
      echo "------------------------------------------------------"
    fi
    echo "Create a folder to put all the binary files."
    echo "------------------------------------------------------"
    echo "mkdir -p target/ci/$PROJECT_BUILD_NAME"
    mkdir -p "target/ci/$PROJECT_BUILD_NAME"
    echo "ls -l target/ci/$PROJECT_BUILD_NAME"
    ls -l "target/ci/$PROJECT_BUILD_NAME"

    echo "------------------------------------------------------"
    echo "cp target/scala-*/*.jar target/ci/$PROJECT_BUILD_NAME/"
    cp target/scala-*/*.jar "target/ci/$PROJECT_BUILD_NAME/"
    echo "------------------------------------------------------"
    echo "ls -lR target/ci/$PROJECT_BUILD_NAME/"
    ls -lR "target/ci/$PROJECT_BUILD_NAME"
    echo "------------------------------------------------------"
    echo "Copying all binary files to 'target/ci', Done!"
    echo "======================================================"

    if sbt checkGithubCredentials releaseOnGithub ; then
      echo "Deploying to GitHub: Done"
      echo "============================================"
    else
      echo "============================================"
      echo "Build and Deploy: Failed"
      echo "============================================"
      exit 1
    fi
  else
    echo "============================================"
    echo "Build and Deploy: Failed"
    echo "============================================"
    exit 1
  fi

  echo "============================================"
  echo "Build and Deploy: Done"
  echo "============================================"
else
  echo "============================================"
  echo "It is not release branch so skip deployment."
  echo "Build: Done"
  echo "============================================"
fi
