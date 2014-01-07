	private class StartWorkflow extends Thread {
		JSONObject vm = null;

		public StartWorkflow(JSONObject vm) {
			this.vm = vm;
		}

		public void run() {
			try {
				String acsk = client.login("ACCESSID", "ACCESSKEY");
				client.addUser(acsk, "operator", "Operator", "operator@null.com", "GMT+08:00", "zh-CN", "E");
				client.addUser(acsk, "enduse_1", "endUser_1", "operator@null.com", "GMT+08:00", "zh-CN", "E");
				String teamId = client.createTeam(acsk, "ASTeam", "Autoscale Team");
				JSONObject tm = new JSONObject();
				tm.put("operator", "Approver");
				tm.put("zhengyongsheng", "PE");

				client.addTeamMembers(acsk, teamId, tm);
				JSONObject att = new JSONObject();
				att.put("PBO", vm);
				JSONObject att2 = new JSONObject();
				client.startWorkflow(acsk, "enduser1", "eed2e9fbd7194252b88ea2dd705febc9", teamId, "AUTOSCALE", att);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

