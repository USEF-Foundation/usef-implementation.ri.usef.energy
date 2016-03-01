# USEF Implementation Starter project

This is a basic Maven project that provides a starting point for developers to create their own deployments with their own PBC's.

When built, this project creates war files for all the projects in usef-deployments.

#### Requirements

A USEF RI 1.3.1 version is expected to be built locally at least once, due to required dependencies.

#### Configuring a PBC.

When adding a PBC to one of these projects, these are not automatically configured.

Each $USEF_HOME/usef-environment/nodes/configuration/\<participant\>/ folder contains a pbc-catalog.properties with the configured PBC's.

Note: When re-running the usef cleanup scripts $USEF_HOME/usef-environment/nodes is deleted!

More information can be found in the USEF RI documentation.
