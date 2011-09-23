@echo off

set fasthome=%~p0
for %%A in (%*) do (
	cd %%A
	mvn clean install -Dmaven.test.skip=true
	cd %fasthome%
)