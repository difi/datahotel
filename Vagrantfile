# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|

	config.vm.customize ["modifyvm", :id, "--memory", 2048, "--name", "Difi Datahotel"]
	config.vm.box = "precise32"
	config.vm.box_url = "http://files.vagrantup.com/precise32.box"
	config.vm.host_name = "dev-datahotel"
	config.vm.network :hostonly, "192.168.50.60"
	config.vm.provision :shell, :path => "vagrant.sh"

end
