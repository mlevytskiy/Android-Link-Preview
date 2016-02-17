package com.library.weblinkpreview2;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.library.weblinkpreview2.linkPreviewContainerImpl.StringUtils;
import com.library.weblinkpreview2.pojo.LinkPreviewPojo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextCrawler {

	private LinkPreviewCallback callback;

	public TextCrawler() {
	}

	public void makePreview(LinkPreviewCallback callback, String url) {
		this.callback = callback;
		new GetCode().execute(url);
	}

	/** Get html code */
	public class GetCode extends AsyncTask<String, Void, Void> {

		private SourceContent sourceContent = new SourceContent();
		private ArrayList<String> urls;

		@Override
		protected void onPreExecute() {
			if (callback != null) {
				callback.onPre();
			}
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			if (callback != null) {
				callback.onPos(sourceContent, isNull());
			}
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(String... params) {
			// Don't forget the http:// or https://
			urls = SearchUrls.matches(params[0]);

			if (urls.size() > 0) {
				try {
					sourceContent.setFinalUrl(urls.get(0));//unshortenUrl(extendedTrim()));
				} catch (Exception e) {
					System.out.print(e);
				}
			} else {
				sourceContent.setFinalUrl("");
			}

			if (!sourceContent.getFinalUrl().equals("")) {
				if (isImage(sourceContent.getFinalUrl())
						&& !sourceContent.getFinalUrl().contains("dropbox")) {
					sourceContent.setSuccess(true);

					sourceContent.setTitle("");
					sourceContent.setDescription("");

				} else {
					try {

						Document doc = Jsoup
								.connect(sourceContent.getFinalUrl())
								.userAgent("Mozilla")
								.get();

						sourceContent.setHtmlCode(extendedTrim(doc.toString()));

						HashMap<String, String> metaTags = getMetaTags(sourceContent
								.getHtmlCode());

						sourceContent.setTitle(metaTags.get("title"));
						sourceContent.setDescription(metaTags
								.get("description"));

						if (sourceContent.getTitle().equals("")) {
							String matchTitle = Regex.pregMatch(
									sourceContent.getHtmlCode(),
									Regex.TITLE_PATTERN, 2);

							if (!matchTitle.equals(""))
								sourceContent.setTitle(htmlDecode(matchTitle));
						}

						if (sourceContent.getDescription().equals(""))
							sourceContent
									.setDescription(crawlCode(sourceContent
											.getHtmlCode()));

						sourceContent.setDescription(sourceContent
								.getDescription().replaceAll(
										Regex.SCRIPT_PATTERN, ""));

						String imageUrl = parsePageHeaderInfo(doc);

						if (!TextUtils.isEmpty(imageUrl)) {
							sourceContent.setImage(imageUrl);
						}

						sourceContent.setSuccess(true);
					} catch (Exception e) {
						sourceContent.setSuccess(false);
					}
				}
			}

			String[] finalLinkSet = sourceContent.getFinalUrl().split("&");
			sourceContent.setUrl(finalLinkSet[0]);

			sourceContent.setDescription(stripTags(sourceContent
					.getDescription()));

			return null;
		}

		/** Verifies if the content could not be retrieved */
		public boolean isNull() {
			return !sourceContent.isSuccess() && 
				extendedTrim(sourceContent.getHtmlCode()).equals("") && 
				!isImage(sourceContent.getFinalUrl());
		}

	}

	public static void fillSourceContent(LinkPreviewPojo pojo, String url) {
		try {
			Document doc = Jsoup
                    .connect(url)
					.timeout(3000)
                    .userAgent("Mozilla")
                    .get();

			Element header = getHeader(doc);
			String imageLink = parseOGImage(header);

			pojo.setTitle("empty");
			pojo.setDescription("description");

			if (TextUtils.isEmpty(imageLink)) {
				return;
			}

			if (StringUtils.isLinkPartialAndNeedMainUrl(imageLink)) {
				try {
					String baseUrl = new URI(url).getHost();
					imageLink = StringUtils.fixIfLinkPartial(imageLink, baseUrl);
				} catch (URISyntaxException e) {
					imageLink = null;
				}
			} else {
				imageLink = StringUtils.fixIfLinkPartial(imageLink, null);
			}
			pojo.setTitle(imageLink);
			pojo.setImageUrl(imageLink);
		} catch (IOException e) {
			Log.e("TextCrawler", "url=" + url, e);
			pojo.setTitle(null);
		}
	}

	/** Gets content from a html tag */
	private static String getTagContent(String tag, String content) {

		String pattern = "<" + tag + "(.*?)>(.*?)</" + tag + ">";
		String result = "", currentMatch = "";

		List<String> matches = Regex.pregMatchAll(content, pattern, 2);
		
		int matchesSize = matches.size();
		for (int i = 0; i < matchesSize; i++) {
			currentMatch = stripTags(matches.get(i));
			if (currentMatch.length() >= 120) {
				result = extendedTrim(currentMatch);
				break;
			}
		}

		if (result.equals("")) {
			String matchFinal = Regex.pregMatch(content, pattern, 2);
			result = extendedTrim(matchFinal);
		}

		result = result.replaceAll("&nbsp;", "");

		return htmlDecode(result);
	}

	/** Transforms from html to normal string */
	private static String htmlDecode(String content) {
		return Jsoup.parse(content).text();
	}

	/** Crawls the code looking for relevant information */
	private static String crawlCode(String content) {
		String result = "";
		String resultSpan = "";
		String resultParagraph = "";
		String resultDiv = "";

		resultSpan = getTagContent("span", content);
		resultParagraph = getTagContent("p", content);
		resultDiv = getTagContent("div", content);

		result = resultSpan;

		if (resultParagraph.length() > resultSpan.length()
				&& resultParagraph.length() >= resultDiv.length())
			result = resultParagraph;
		else if (resultParagraph.length() > resultSpan.length()
				&& resultParagraph.length() < resultDiv.length())
			result = resultDiv;
		else
			result = resultParagraph;

		return htmlDecode(result);
	}

	/** Strips the tags from an element */
	private static String stripTags(String content) {
		return Jsoup.parse(content).text();
	}

	/** Verifies if the url is an image */
	private boolean isImage(String url) {
		return url.matches(Regex.IMAGE_PATTERN);
	}

	/**
	 * Returns meta tags from html code
	 */
	private static HashMap<String, String> getMetaTags(String content) {

		HashMap<String, String> metaTags = new HashMap<String, String>();
		metaTags.put("url", "");
		metaTags.put("title", "");
		metaTags.put("description", "");
		metaTags.put("image", "");

		List<String> matches = Regex.pregMatchAll(content,
				Regex.METATAG_PATTERN, 1);

		for (String match : matches) {
			if (match.toLowerCase().contains("property=\"og:url\"")
					|| match.toLowerCase().contains("property='og:url'")
					|| match.toLowerCase().contains("name=\"url\"")
					|| match.toLowerCase().contains("name='url'"))
				metaTags.put("url", separeMetaTagsContent(match));
			else if (match.toLowerCase().contains("property=\"og:title\"")
					|| match.toLowerCase().contains("property='og:title'")
					|| match.toLowerCase().contains("name=\"title\"")
					|| match.toLowerCase().contains("name='title'"))
				metaTags.put("title", separeMetaTagsContent(match));
			else if (match.toLowerCase()
					.contains("property=\"og:description\"")
					|| match.toLowerCase()
					.contains("property='og:description'")
					|| match.toLowerCase().contains("name=\"description\"")
					|| match.toLowerCase().contains("name='description'"))
				metaTags.put("description", separeMetaTagsContent(match));
			else if (match.toLowerCase().contains("property=\"og:image\"")
					|| match.toLowerCase().contains("property='og:image'")
					|| match.toLowerCase().contains("name=\"image\"")
					|| match.toLowerCase().contains("name='image'"))
				metaTags.put("image", separeMetaTagsContent(match));
		}

		return metaTags;
	}

	/** Gets content from metatag */
	private static String separeMetaTagsContent(String content) {
		String result = Regex.pregMatch(content, Regex.METATAG_CONTENT_PATTERN,
				1);
		return htmlDecode(result);
	}

	/** Removes extra spaces and trim the string */
	public static String extendedTrim(String content) {
		return content.replaceAll("\\s+", " ").replace("\n", " ")
				.replace("\r", " ").trim();
	}

	private static Element getHeader(Document doc) {
		return doc.head();
	}

	private static String parseOGImage(Element header) {
		Elements elements = header.select("meta[property~=og:image]");
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return elements.last().attr("content");
	}

	private static String parseOGBaseUrl(Element header) {
		Elements elements = header.select("meta[property~=og:url");
		if (elements == null || elements.size() == 0) {
			return null;
		}
		return elements.last().attr("content");
	}

	private static String parsePageHeaderInfo(Document doc) {
		Elements elements = doc.head().select("link[href~=.*\\.(ico|png)]");
		if (elements.isEmpty()) {
			elements = doc.head().select("link[rel~=.*\\.(ico|png)]");
		}
		Element bigIcon = null;
		int size = 0;
		for (Element element : elements) {
			String sizes = element.attr("sizes");
			if (!TextUtils.isEmpty(sizes)) {
				if (bigIcon == null) {
					bigIcon = element;
					size = getIntSize(sizes);
				} else {
					int currentSize = getIntSize(sizes);
					if (currentSize > size) {
						size = currentSize;
						bigIcon = element;
					}
				}
			}
		}
		if (bigIcon != null) {
			return bigIcon.attr("href");
		} else {
			return elements.last().attr("href");
		}
	}

	private static String parsePageHeaderInfo(String urlStr) throws Exception {
		Document doc = Jsoup.connect(urlStr).userAgent("Mozilla").get();
		return parsePageHeaderInfo(doc);
	}

	private static int getIntSize(String sizes) {
		int index = sizes.indexOf('x');
		if (index == -1) {
			return 0;
		}
		return Integer.valueOf(sizes.substring(0, index));
	}

}
