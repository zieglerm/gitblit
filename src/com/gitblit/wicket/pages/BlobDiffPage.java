package com.gitblit.wicket.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.gitblit.GitBlit;
import com.gitblit.Keys;
import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.JGitUtils.DiffOutputType;
import com.gitblit.utils.StringUtils;
import com.gitblit.wicket.RepositoryPage;
import com.gitblit.wicket.WicketUtils;
import com.gitblit.wicket.panels.CommitHeaderPanel;
import com.gitblit.wicket.panels.PathBreadcrumbsPanel;

public class BlobDiffPage extends RepositoryPage {

	public BlobDiffPage(PageParameters params) {
		super(params);

		final String blobPath = WicketUtils.getPath(params);
		final String baseObjectId = WicketUtils.getBaseObjectId(params);

		Repository r = getRepository();
		RevCommit commit = getCommit();
				
		DiffOutputType diffType = DiffOutputType.forName(GitBlit.self().settings().getString(Keys.web.diffStyle, DiffOutputType.GITBLIT.name()));

		String diff;
		if (StringUtils.isEmpty(baseObjectId)) {
			// use first parent
			diff = JGitUtils.getCommitDiff(r, commit, blobPath, diffType);
			add(new BookmarkablePageLink<Void>("patchLink", PatchPage.class, WicketUtils.newPathParameter(repositoryName, objectId, blobPath)));
		} else {
			// base commit specified
			RevCommit baseCommit = JGitUtils.getCommit(r, baseObjectId);
			diff = JGitUtils.getCommitDiff(r, baseCommit, commit, blobPath, diffType);
			add(new BookmarkablePageLink<Void>("patchLink", PatchPage.class, WicketUtils.newBlobDiffParameter(repositoryName, baseObjectId, objectId, blobPath)));
		}
		
		add(new BookmarkablePageLink<Void>("commitLink", CommitPage.class, WicketUtils.newObjectParameter(repositoryName, objectId)));
		add(new BookmarkablePageLink<Void>("commitDiffLink", CommitDiffPage.class, WicketUtils.newObjectParameter(repositoryName, objectId)));

		// diff page links
		add(new Label("blameLink", getString("gb.blame")));
		add(new BookmarkablePageLink<Void>("historyLink", HistoryPage.class, WicketUtils.newPathParameter(repositoryName, objectId, blobPath)));

		add(new CommitHeaderPanel("commitHeader", repositoryName, commit));

		add(new PathBreadcrumbsPanel("breadcrumbs", repositoryName, blobPath, objectId));

		add(new Label("diffText", diff).setEscapeModelStrings(false));
	}

	@Override
	protected String getPageName() {
		return getString("gb.diff");
	}
}
