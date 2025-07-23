require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

puts "Min iOS version: #{min_ios_version_supported} (from .podspec)"

Pod::Spec.new do |s|
  s.name         = "ReactNativeCmp"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/SourcePointUSA/react-native-sourcepoint-cmp.git", :tag => "#{s.version}" }

  s.dependency "ConsentViewController", "7.11.1"
  s.source_files = "ios/**/*.{h,m,mm,cpp,swift}"
  s.private_header_files = "ios/**/*.h"

 install_modules_dependencies(s)
end
