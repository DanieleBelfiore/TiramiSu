# TiramiSu!
An Android App to help to think positive, every day a new positive thought!

Think positive, having more control over the flow of our thoughts means investing in the quality of our life.
Happiness starts from what happens inside us, not outside.

With this application you will have the opportunity every day to receive a new positive thought from great writers of the past and present to help you live your life to the fullest with culture and smile!

*"To be happy you have to train every day" - Alberto Casiraghy*

<img alt="Logo" src="logo.png" height="150" />

<a href="https://play.google.com/store/apps/details?id=com.dreamingbetter.tiramisu" target="_blank">
    <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="100" />
</a>

## How To Use This Code

If you want edit this project, you have to clone this repo, then add the **google-services.json** about the project of your [Firebase Console](https://console.firebase.google.com/).

In this project I used [Firebase Realtime Database](https://firebase.google.com/docs/database) to store quotes and if you want to use it, you have to create a json file with a data model like this:

```json
{
	"debug": [
		{
			"version": 1, "lang": "it",
			"contents": [
				{
					"uid": "",
					"author": "",
					"text": ""
				}
            ]
        },
        {
			"version": 1, "lang": "en",
			"contents": [
				{
					"uid": "",
					"author": "",
					"text": ""
				}
            ]
        }
    ],
    "release": [
		{
			"version": 1, "lang": "it",
			"contents": [
				{
					"uid": "",
					"author": "",
					"text": ""
				}
            ]
        },
        {
			"version": 1, "lang": "en",
			"contents": [
				{
					"uid": "",
					"author": "",
					"text": ""
				}
            ]
        }
    ]
}
```

You can import this json file to insert the contents in the **Firebase Realtime Database**.

If you needs more explanations, please [contact me](mailto:danielebelfiorepc@gmail.com).

## Clarification

This project is written in the little free time available, it could certainly be better written and structured in a more formal way, but for me it is above all a pastime.
Having said that, I am always willing to improve it with the help of anyone who is willing to help and make constructive criticisms.

## Versioning

I use [SemVer](http://semver.org) for versioning. 

For the versions available, see the **tags** on this repository.

## Contributing

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/mit-license.php).