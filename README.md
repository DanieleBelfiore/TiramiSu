<!-- General Meta Tags -->
<meta charset="text/html; charset=utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=text/html; charset=utf-8">
<meta name="language" content="English">
<meta name="robots" content="index, follow">
<meta name="revisit-after" content="8 days">

<!-- Default Meta Tags -->
<meta name="title" content="TiramiSu!">
<meta name="description" content="An Android App to help to think positive, every day a new positive thought!">
<meta name="author" content="Daniele Belfiore">
<meta name="keywords" content="think positive, positive thought, tiramisu, happiness, android">
<meta name="url" content="https://github.com/DanieleBelfiore/TiramiSu">
<meta name="image" content="https://github.com/DanieleBelfiore/TiramiSu/raw/master/logo.png">

<!-- Google / Search Engine Tags -->
<meta itemprop="title" content="TiramiSu!">
<meta itemprop="description" content="An Android App to help to think positive, every day a new positive thought!">
<meta itemprop="author" content="Daniele Belfiore">
<meta itemprop="keywords" content="think positive, positive thought, tiramisu, happiness, android">
<meta itemprop="url" content="https://github.com/DanieleBelfiore/TiramiSu">
<meta itemprop="image" content="https://github.com/DanieleBelfiore/TiramiSu/raw/master/logo.png">

<!-- Facebook Meta Tags -->
<meta property="og:title" content="TiramiSu!">
<meta property="og:description" content="An Android App to help to think positive, every day a new positive thought!">
<meta property="og:author" content="Daniele Belfiore">
<meta property="og:keywords" content="think positive, positive thought, tiramisu, happiness, android">
<meta property="og:url" content="https://github.com/DanieleBelfiore/TiramiSu">
<meta property="og:image" content="https://github.com/DanieleBelfiore/TiramiSu/raw/master/logo.png">

<!-- Twitter Meta Tags -->
<meta name="twitter:title" content="TiramiSu!">
<meta name="twitter:description" content="An Android App to help to think positive, every day a new positive thought!">
<meta name="twitter:author" content="Daniele Belfiore">
<meta name="twitter:keywords" content="think positive, positive thought, tiramisu, happiness, android">
<meta name="twitter:url" content="https://github.com/DanieleBelfiore/TiramiSu">
<meta name="twitter:image" content="https://github.com/DanieleBelfiore/TiramiSu/raw/master/logo.png">

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

I manage the quotes through a Google Spreadsheet doc, with a sheet for each language and every sheet has three columns (the structure of contents):
- uid
- author
- text

Then I create the json file starting from the doc through this [Google App Script](Export.gs) that sort alphabetically the doc and create the json file to import in the **Firebase Realtime Database**.

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
